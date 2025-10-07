package com.jss.osiris.modules.osiris.reporting.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidgetSerie;
import com.jss.osiris.modules.osiris.reporting.repository.ReportingWidgetRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ReportingWidgetServiceImpl implements ReportingWidgetService {

    @Autowired
    ReportingWidgetRepository reportingWidgetRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public List<ReportingWidget> getReportingWidgets() {
        return IterableUtils.toList(reportingWidgetRepository.findAll());
    }

    @Override
    public List<ReportingWidget> getReportingWidgetsByFrequency(String reportingFrequency) {
        return reportingWidgetRepository.findByReportingUpdateFrequency(reportingFrequency);
    }

    @Override
    public ReportingWidget getReportingWidget(Integer id) {
        Optional<ReportingWidget> reportingWidget = reportingWidgetRepository.findById(id);
        if (reportingWidget.isPresent())
            return reportingWidget.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReportingWidget addOrUpdateReportingWidget(
            ReportingWidget reportingWidget) {
        if (reportingWidget.getReportingWidgetSeries() != null)
            for (ReportingWidgetSerie serie : reportingWidget.getReportingWidgetSeries())
                serie.setReportingWidget(reportingWidget);
        return reportingWidgetRepository.save(reportingWidget);
    }

    @Override
    public String getReportingWidgetPayload(ReportingWidget widget) {
        return widget.getPayload();
    }

    @Override
    public void computeReportingWidget(Integer widgetId) throws OsirisException {
        ReportingWidget widget = getReportingWidget(widgetId);
        boolean computeEvolution = true;
        if (widget != null) {

            // Series generation
            StringBuilder seriesUnion = new StringBuilder();
            if (widget.getReportingWidgetSeries() != null)
                for (ReportingWidgetSerie serie : widget.getReportingWidgetSeries()) {
                    if (seriesUnion.length() > 0) {
                        seriesUnion.append(" UNION ALL ");
                    }

                    if ("table".equalsIgnoreCase(serie.getPlotType())
                            || "boxplot".equalsIgnoreCase(serie.getPlotType())
                            || "sankey".equalsIgnoreCase(serie.getPlotType())) {
                        computeEvolution = false;
                        seriesUnion.append(String.format(
                                """
                                        SELECT '%s' as serie_name,
                                               '%s' as plot_type,
                                               '%s' as stack,
                                               jsonb_build_array(%s) as columns,
                                               (SELECT jsonb_agg(to_jsonb(s)) FROM (%s) s) as data
                                        """,
                                serie.getSerieName().replace("'", "''"),
                                serie.getPlotType(), serie.getStack() != null ? serie.getStack() : "",
                                serie.getColumns() == null ? ""
                                        : Arrays.stream(serie.getColumns().split(","))
                                                .map(col -> "'" + col.trim() + "'")
                                                .collect(Collectors.joining(",")),
                                serie.getSerieSqlText()));
                    } else {
                        seriesUnion.append(String.format(
                                "SELECT '%s' as serie_name, '%s' as plot_type,'%s' as stack, NULL::jsonb as columns, jsonb_agg(jsonb_build_array(label, value, dim) ) as data "
                                        +
                                        "FROM (%s) s",
                                serie.getSerieName().replace("'", "''"),
                                serie.getPlotType(), serie.getStack() != null ? serie.getStack() : "",
                                serie.getSerieSqlText()));
                    }
                }

            String finalSql = String.format("""
                    UPDATE reporting_widget
                    SET payload =t.payload,
                     last_update = now()
                     from (
                        WITH  series_data AS (%s)
                        SELECT jsonb_build_object(
                            'series', (
                                SELECT jsonb_agg(
                                    jsonb_build_object(
                                        'name', serie_name,
                                        'type', plot_type,
                                        'stack', stack,
                                        'columns', columns,
                                        'data', data
                                    )
                                )
                                FROM series_data
                            )
                        ) as payload
                    ) t
                    WHERE id = %d
                    """, seriesUnion.toString(), widgetId);

            // 4. Exécution native
            em.createNativeQuery(finalSql).executeUpdate();

            // Update last value
            if (computeEvolution && widget.getLabelType().equals(ReportingWidget.LABEL_TYPE_DATETIME)) {
                widget = getReportingWidget(widgetId);
                if (widget.getPayload() != null) {
                    JsonNode root;
                    try {
                        root = objectMapper.readTree(widget.getPayload());
                    } catch (JsonMappingException e) {
                        throw new OsirisException(e, "Error when parsing payload of widget " + widgetId);
                    } catch (JsonProcessingException e) {
                        throw new OsirisException(e, "Error when parsing payload of widget " + widgetId);
                    }

                    ArrayNode series = (ArrayNode) root.get("series");
                    if (series != null && series.size() > 0) {
                        JsonNode serie = series.get(0);

                        ArrayNode data = (ArrayNode) serie.get("data");
                        if (data != null) {
                            BigDecimal last = null;
                            BigDecimal penultieme = null;
                            if (data.size() > 0)
                                last = new BigDecimal(data.get(data.size() - 1).get(1).asDouble());
                            if (data.size() > 1)
                                penultieme = new BigDecimal(data.get(data.size() - 2).get(1).asDouble());

                            widget.setLastValue(last);
                            if (penultieme != null && !last.equals(new BigDecimal(0))) {
                                widget.setCurrentEvolution(
                                        last.subtract(penultieme).divide(last, RoundingMode.HALF_UP)
                                                .setScale(2, RoundingMode.HALF_EVEN)
                                                .multiply(new BigDecimal(100.0)));
                            }
                            addOrUpdateReportingWidget(widget);
                        }
                    }
                }
            }
        }
    }
}
