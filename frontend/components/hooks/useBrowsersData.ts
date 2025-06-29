import { useCallback, useEffect, useState } from 'react'

interface BrowserDataRow {
	browser: string
	users: string
}

interface UseBrowsersDataResult {
	data: BrowserDataRow[]
	loading: boolean
	error: string | null
	refetch: () => void
}

export const useBrowsersData = (
	startDate = '7daysAgo',
	endDate = 'today'
): UseBrowsersDataResult => {
	const [data, setData] = useState<BrowserDataRow[]>([])
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState<string | null>(null)

	const fetchData = useCallback(async () => {
		setLoading(true)
		setError(null)
		try {
			const res = await fetch(
				`/api/analytics/browsers?startDate=${startDate}&endDate=${endDate}`
			)
			const json = await res.json()
			if (res.ok) {
				setData(json)
			} else {
				setError(
					json.details || json.error || 'Ошибка получения данных о браузерах'
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
