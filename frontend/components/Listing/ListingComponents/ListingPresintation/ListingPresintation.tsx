import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { ProfileTitle } from '@/components/Profile/ProfileContent/ProfileContent.styled'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import { FC } from 'react'
import { RiDownload2Fill } from 'react-icons/ri'
import { ListingSpecialist } from '../../Listing.styled'
import { ListingButton } from '../ListingButton'
interface ListingPresentationProps {
	listingPresintation?: Record<string, unknown> | null
}

export const ListingPresentation: FC<ListingPresentationProps> = ({
	listingPresintation,
}) => {
	let presentationUrl: string | undefined
	let financialModelUrl: string | undefined
	let businessPlanUrl: string | undefined
	const router = useRouter()
	const { user } = useCatalog()
	const { t } = useTranslation('common')
	if (listingPresintation && typeof listingPresintation === 'object') {
		Object.entries(listingPresintation).forEach(([key, value]) => {
			if (typeof value === 'string' && value.length > 0) {
				if (key === 'Презентация') {
					presentationUrl = value
				} else if (key === 'Финансовая модель') {
					financialModelUrl = value
				} else if (key === 'Бизнес план') {
					businessPlanUrl = value
				}
			}
		})
	}

	const hasAnyDocuments =
		presentationUrl || financialModelUrl || businessPlanUrl

	const handleDownload = (url: string) => () => {
		if (!user) {
			router.push('/login')
			return
		}
		window.open(url, '_blank')
	}

	return hasAnyDocuments ? (
		<>
			<ListingSpecialist>
				<ProfileTitle
					bgc={colors.btnMainColor}
					style={{ fontSize: '21px', marginBottom: '20px' }}
				>
					{t('listing.documents')}
				</ProfileTitle>

				{presentationUrl && (
					<ListingButton
						title={t('listing.downloadPresentation')}
						image={<RiDownload2Fill />}
						onClick={handleDownload(presentationUrl)}
						bgcolor={`${colors.btnMainColor}`}
						color={`${colors.mainWhiteTextColor}`}
						border={`none`}
					/>
				)}

				{financialModelUrl && (
					<ListingButton
						title={t('listing.downloadFinancialModel')}
						image={<RiDownload2Fill />}
						onClick={handleDownload(financialModelUrl)}
						bgcolor={`${colors.btnMainColor}`}
						color={`${colors.mainWhiteTextColor}`}
						border={`none`}
					/>
				)}

				{businessPlanUrl && (
					<ListingButton
						title={t('listing.downloadBisPlan')}
						image={<RiDownload2Fill />}
						onClick={handleDownload(businessPlanUrl)}
						bgcolor={`${colors.btnMainColor}`}
						color={`${colors.mainWhiteTextColor}`}
						border={`none`}
					/>
				)}
			</ListingSpecialist>
		</>
	) : null
}
