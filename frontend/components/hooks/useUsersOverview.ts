import { useCallback, useEffect, useState } from 'react'

interface UsersOverviewRow {
	date: string
	totalUsers: string
	newUsers: string
	formattedDate?: string
}

interface UseUsersOverviewResult {
	data: UsersOverviewRow[]
	loading: boolean
	error: string | null
	refetch: () => void
}

const formatDate = (rawDate: string): string => {
	const year = rawDate.slice(0, 4)
	const month = rawDate.slice(4, 6)
	const day = rawDate.slice(6, 8)
	return `${day}.${month}.${year}`
}

const sortByDate = (data: UsersOverviewRow[]) =>
	[...data].sort((a, b) => Number(a.date) - Number(b.date))

export const useUsersOverview = (
	startDate = '7daysAgo',
	endDate = 'today'
): UseUsersOverviewResult => {
	const [data, setData] = useState<UsersOverviewRow[]>([])
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState<string | null>(null)

	const fetchData = useCallback(async () => {
		setLoading(true)
		setError(null)
		try {
			const res = await fetch(
				`/api/analytics/users-overview?startDate=${startDate}&endDate=${endDate}`
			)
			const json = await res.json()
			if (res.ok) {
				const processed = sortByDate(json).map((item: UsersOverviewRow) => ({
					...item,
					formattedDate: formatDate(item.date),
				}))
				setData(processed)
			} else {
				setError(
					json.details ||
						json.error ||
						'Ошибка получения данных о пользователях'
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
