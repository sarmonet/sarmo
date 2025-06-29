import { getPackagingDetails } from '@/services/administration'
import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next' // Импортируем хук useTranslation
import { FaCheckCircle } from 'react-icons/fa'
import { IoCloseCircle } from 'react-icons/io5'
import { IPackagingDetails } from '../AdministrationAcc/Admin.interface'
import {
	TransactionBlock,
	TransactionBlocks,
	TransactionTop,
	TransactionWrapper,
} from '../AdministrationTransaction/AdministrationTransaction.styled'

export const AdministrationPackaging = () => {
	const [packagingDetails, setPackagingDetails] = useState<IPackagingDetails[]>(
		[]
	)
	const { t } = useTranslation() // Инициализируем хук перевода

	useEffect(() => {
		const fetchPackagingDetails = async () => {
			try {
				const response = await getPackagingDetails()
				setPackagingDetails(response)
			} catch (error) {
				console.error(
					`${t('administrationPackaging.errorFetchingPackagingDetails')} `,
					error
				)
			}
		}

		fetchPackagingDetails()
	}, [t]) // Добавляем t в зависимости useEffect

	return (
		<div>
			<TransactionWrapper>
				<TransactionBlocks>
					<TransactionTop gridColumns={5}>
						<p>{t('administrationPackaging.userId')}</p>
						<p>{t('administrationPackaging.listingId')}</p>
						<p>{t('administrationPackaging.presentation')}</p>
						<p>{t('administrationPackaging.design')}</p>
						<p>{t('administrationPackaging.financialModel')}</p>
					</TransactionTop>
					{packagingDetails &&
						packagingDetails.map(item => (
							<TransactionBlock gridColumns={5} key={item.userId}>
								<p>{item.userId}</p>
								<p>{item.listingId}</p>
								<p>
									{item.presentationSelected ? (
										<FaCheckCircle fill='green' size={22} />
									) : (
										<IoCloseCircle fill='red' size={23} />
									)}
								</p>
								<p>
									{item.financialModelSelected ? (
										<FaCheckCircle fill='green' size={22} />
									) : (
										<IoCloseCircle fill='red' size={23} />
									)}
								</p>
								<p>
									{item.pageDesignSelected ? (
										<FaCheckCircle fill='green' size={22} />
									) : (
										<IoCloseCircle fill='red' size={23} />
									)}
								</p>
							</TransactionBlock>
						))}
				</TransactionBlocks>
			</TransactionWrapper>
		</div>
	)
}
