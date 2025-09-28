package com.jss.osiris.modules.osiris.reporting.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void computeReportingWidget(Integer widgetId) {
        ReportingWidget widget = getReportingWidget(widgetId);
        if (widget != null) {

            // Series generation
            StringBuilder seriesUnion = new StringBuilder();
            if (widget.getReportingWidgetSeries() != null)
                for (ReportingWidgetSerie serie : widget.getReportingWidgetSeries()) {
                    if (seriesUnion.length() > 0) {
                        seriesUnion.append(" UNION ALL ");
                    }

                    if ("table".equalsIgnoreCase(serie.getPlotType())) {
                        seriesUnion.append(String.format(
                                """
                                        SELECT '%s' as serie_name,
                                               '%s' as plot_type,
                                               jsonb_build_array(%s) as columns,
                                               (SELECT jsonb_agg(to_jsonb(s)) FROM (%s) s) as data
                                        """,
                                serie.getSerieName().replace("'", "''"),
                                serie.getPlotType(),
                                Arrays.stream(serie.getColumns().split(","))
                                        .map(col -> "'" + col.trim() + "'")
                                        .collect(Collectors.joining(",")),
                                serie.getSerieSqlText()));
                    } else {
                        seriesUnion.append(String.format(
                                "SELECT '%s' as serie_name, '%s' as plot_type, NULL::jsonb as columns, jsonb_agg(value ) as data "
                                        +
                                        "FROM (%s) s",
                                serie.getSerieName().replace("'", "''"),
                                serie.getPlotType(),
                                serie.getSerieSqlText()));
                    }
                }

            String finalSql = String.format("""
                    UPDATE reporting_widget
                    SET payload =t.payload,
                     last_update = now()
                     from (
                        WITH lbl AS (%s),
                        series_data AS (%s)
                        SELECT jsonb_build_object(
                            'labels', (SELECT jsonb_agg(label) FROM lbl),
                            'series', (
                                SELECT jsonb_agg(
                                    jsonb_build_object(
                                        'name', serie_name,
                                        'type', plot_type,
                                        'columns', columns,
                                        'data', data
                                    )
                                )
                                FROM series_data
                            )
                        ) as payload
                    ) t
                    WHERE id = %d
                    """, widget.getLabelSqlText(), seriesUnion.toString(), widgetId);

            // 4. Exécution native
            em.createNativeQuery(finalSql).executeUpdate();
        }
    }
}
