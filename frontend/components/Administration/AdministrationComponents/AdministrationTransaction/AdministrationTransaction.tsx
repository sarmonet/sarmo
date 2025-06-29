import { getTransactionSupport } from '@/services/administration'
import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next' // Импортируем хук useTranslation
import { FaCheckCircle } from 'react-icons/fa'
import { IoChatbubbleOutline, IoCloseCircle } from 'react-icons/io5'
import { ITransactionSupport } from '../AdministrationAcc/Admin.interface'
import {
	TransactionBlock,
	TransactionBlocks,
	TransactionTop,
	TransactionWrapper,
} from './AdministrationTransaction.styled'

export const AdministrationTransaction = () => {
	const [transactionSupports, setTransactionSupports] = useState<
		ITransactionSupport[]
	>([])
	const { t } = useTranslation() // Инициализируем хук перевода

	useEffect(() => {
		const fetchTransactionSupport = async () => {
			try {
				const response = await getTransactionSupport()
				setTransactionSupports(response)
			} catch (error) {
				console.error(
					`${t('administrationTransaction.errorFetchingSupport')} `,
					error
				)
			}
		}

		fetchTransactionSupport()
	}, [t]) // Добавляем t в зависимости useEffect

	return (
		<div>
			<div>
				<TransactionWrapper>
					<TransactionBlocks>
						<TransactionTop>
							<p>{t('administrationTransaction.investorId')}</p>
							<p>{t('administrationTransaction.listingId')}</p>
							<p>{t('administrationTransaction.negotiationsAndAnalysis')}</p>
							<p>
								{t('administrationTransaction.preliminaryContractConclusion')}
							</p>
							<p>{t('administrationTransaction.businessDueDiligence')}</p>
							<p>{t('administrationTransaction.financialAnalysis')}</p>
							<p>{t('administrationTransaction.mainContractConclusion')}</p>
							<p>{t('administrationTransaction.postDealSupport')}</p>
						</TransactionTop>
						{transactionSupports.map(transactionSupport => (
							<TransactionBlock key={transactionSupport?.userId}>
								<p>
									{transactionSupport?.userId}{' '}
									<button>
										<IoChatbubbleOutline size={24} />
									</button>{' '}
								</p>
								<p>{transactionSupport?.listingId}</p>
								<p>
									{transactionSupport?.negotiationsAndAnalysis ? (
										<FaCheckCircle fill='green' size={21} />
									) : (
										<IoCloseCircle fill='red' size={23} />
									)}
								</p>
								<p>
									{transactionSupport?.preliminaryContractConclusion ? (
										<FaCheckCircle fill='green' size={21} />
									) : (
										<IoCloseCircle fill='red' size={23} />
									)}
								</p>
								<p>
									{transactionSupport?.businessDueDiligence ? (
										<FaCheckCircle fill='green' size={21} />
									) : (
										<IoCloseCircle fill='red' size={23} />
									)}
								</p>
								<p>
									{transactionSupport?.financialAnalysis ? (
										<FaCheckCircle fill='green' size={21} />
									) : (
										<IoCloseCircle fill='red' size={23} />
									)}
								</p>
								<p>
									{transactionSupport?.mainContractConclusion ? (
										<FaCheckCircle fill='green' size={21} />
									) : (
										<IoCloseCircle fill='red' size={23} />
									)}
								</p>
								<p>
									{transactionSupport?.postDealSupport ? (
										<FaCheckCircle fill='green' size={21} />
									) : (
										<IoCloseCircle fill='red' size={23} />
									)}
								</p>
							</TransactionBlock>
						))}
					</TransactionBlocks>
				</TransactionWrapper>
			</div>
		</div>
	)
}
