// components/hooks/useBounceRateData.ts
import { useCallback, useEffect, useState } from 'react'

interface BounceRateDayData {
	date: string
	bounceRate: string
}

interface UseBounceRateDataResult {
	data: BounceRateDayData[]
	loading: boolean
	error: string | null
	refetch: () => void
}

export const useBounceRateData = (
	startDate = '30daysAgo',
	endDate = 'today'
): UseBounceRateDataResult => {
	const [data, setData] = useState<BounceRateDayData[]>([])
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState<string | null>(null)

	const fetchData = useCallback(async () => {
		setLoading(true)
		setError(null)

		const apiUrl = `/api/analytics/bounce-rate?startDate=${startDate}&endDate=${endDate}`

		try {
			const res = await fetch(apiUrl)
			const json = await res.json()

			if (res.ok) {
				if (Array.isArray(json)) {
					setData(json)
				} else {
					console.error(
						'[useBounceRateData] API returned unexpected data format:',
						json
					)
					setError('Неожиданный формат данных от API показателя отказов.')
				}
			} else {
				const errorMessage =
					json.details ||
					json.error ||
					'Ошибка получения данных о показателе отказов'
				console.error(
					'[useBounceRateData] API returned an error:',
					errorMessage,
					json
				)
				setError(errorMessage)
			}
		} catch (err) {
			const errorMessage = (err as Error).message
			console.error('[useBounceRateData] Fetch failed:', errorMessage, err)
			setError(errorMessage)
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
