export type AnalyticStatsType = {
    id: number,
    icon: string;
    count: {
        prefix?: string,
        value: number,
        suffix?: string
    },
    title: string
    percentage: number;
    percentageIcon: string;
    isPositive: boolean
}