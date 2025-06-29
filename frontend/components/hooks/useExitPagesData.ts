import { useCallback, useEffect, useState } from 'react'

interface ExitPagesRow {
	pagePath: string
	exits: string
	views: string
	exitRate: string
}

interface UseExitPagesDataResult {
	data: ExitPagesRow[]
	loading: boolean
	error: string | null
	refetch: () => void
}

export const useExitPagesData = (
	startDate = '7daysAgo',
	endDate = 'today',
	limit = 10
): UseExitPagesDataResult => {
	const [data, setData] = useState<ExitPagesRow[]>([])
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState<string | null>(null)

	const fetchData = useCallback(async () => {
		setLoading(true)
		setError(null)

		const apiUrl = `/api/analytics/exit-pages?startDate=${startDate}&endDate=${endDate}&limit=${limit}`

		try {
			const res = await fetch(apiUrl)
			const json = await res.json()

			if (res.ok) {
				setData(json)
			} else {
				const errorMessage =
					json.details ||
					json.error ||
					'Ошибка получения данных о страницах выхода'
				console.error(
					'[useExitPagesData] API returned an error:',
					errorMessage,
					json
				)
				setError(errorMessage)
			}
		} catch (err) {
			const errorMessage = (err as Error).message
			console.error('[useExitPagesData] Fetch failed:', errorMessage, err)
			setError(errorMessage)
		} finally {
			setLoading(false)
			console.log('[useExitPagesData] Loading finished.')
		}
	}, [startDate, endDate, limit])

	useEffect(() => {
		console.log('[useExitPagesData] Effect triggered, calling fetchData...')
		fetchData()
	}, [fetchData])

	const refetch = () => {
		console.log('[useExitPagesData] Refetch requested.')
		fetchData()
	}

	return { data, loading, error, refetch }
}
