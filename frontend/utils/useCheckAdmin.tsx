import { useEffect, useState } from 'react'

export function useCheckAdmin() {
	const [isAdmin, setIsAdmin] = useState<boolean | null>(null)

	useEffect(() => {
		const checkAdmin = async () => {
			try {
				const token = localStorage.getItem('accessToken')
				const response = await fetch(
					'https://sarmo.net/api/v1/listing/inactive',
					{
						method: 'GET',
						headers: {
							Accept: 'application/json',
							'Content-Type': 'application/json',
							Authorization: token ? `Bearer ${token}` : '',
						},
						credentials: 'include',
					}
				)

				if (response.status === 403) {
					setIsAdmin(false)
				} else {
					setIsAdmin(response.ok)
				}
			} catch (error) {
				console.error('Ошибка проверки админки:', error)
				setIsAdmin(false)
			}
		}

		checkAdmin()
	}, [])

	return isAdmin
}
