import { useCallback, useEffect, useState } from 'react'

interface TrafficSourceDataRow {
	channelGroup: string
	users: string
}

interface UseTrafficSourcesDataResult {
	data: TrafficSourceDataRow[]
	loading: boolean
	error: string | null
	refetch: () => void
}

export const useTrafficSourcesData = (
	startDate = '7daysAgo',
	endDate = 'today'
): UseTrafficSourcesDataResult => {
	const [data, setData] = useState<TrafficSourceDataRow[]>([])
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState<string | null>(null)

	const fetchData = useCallback(async () => {
		setLoading(true)
		setError(null)
		try {
			const res = await fetch(
				`/api/analytics/traffic-sources?startDate=${startDate}&endDate=${endDate}`
			)
			const json = await res.json()
			if (res.ok) {
				setData(json)
			} else {
				setError(
					json.details ||
						json.error ||
						'Ошибка получения данных об источниках трафика'
				)
			}
		} catch (err) {
			setError((err as Error).message)
		} finally {
			setLoading(false)
		}
	}, [startDate, endDate])

	useEffect(() => {
		fetchData()
	}, [fetchData])

	const refetch = () => {
		fetchData()
	}

	return { data, loading, error, refetch }
}
