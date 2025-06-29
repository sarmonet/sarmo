import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

export const GetTranslate = content => {
	const { t, i18n } = useTranslation()
	const [, setTranslation] = useState('')

	useEffect(() => {
		const fetchTranslation = async () => {
			const message = t({ content })
			setTranslation(message)
		}

		fetchTranslation()
	}, [i18n.language, t, content])
}
