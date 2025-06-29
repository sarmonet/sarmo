import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import { useState } from 'react'
import { GoCheckCircle } from 'react-icons/go'
// import { MdKeyboardArrowRight } from 'react-icons/md'
import { ListingRightInfo } from '../../Listing.styled'
import { ListingButton } from '../ListingButton'
interface ListingRightAboutProps {
	listingId: number
	createdAt: string
	updatedAt: string
	investor?: boolean
	setIsSupportVisible: (visible: boolean) => void
}

export const ListingRightAbout = ({
	listingId,
	createdAt,
	updatedAt,
	investor,
	setIsSupportVisible,
}: ListingRightAboutProps) => {
	const [tooltip, setTooltip] = useState<boolean | null>(null)
	const { user } = useCatalog()
	const { t } = useTranslation('common')
	return (
		<>
			<ListingRightInfo>
				<li>
					{t('listing.id')} <span>{listingId}</span>
				</li>
				<li>
					{t('listing.createdAt')} <span>{createdAt}</span>
				</li>
				<li>
					{t('listing.changed')} <span>{updatedAt}</span>
				</li>
			</ListingRightInfo>

			{!investor && (
				<>
					{/* <ListingButton
						title={t('listing.site')}
						image={<MdKeyboardArrowRight />}
						bgcolor='transparent'
						color={colors.SecondGreyTextColor}
						border={`1px solid ${colors.borderColor}`}
					/> */}
					<button
						onMouseEnter={() => setTooltip(true)}
						onMouseLeave={() => setTooltip(false)}
						className='relative'
					>
						<ListingButton
							title={t('listing.support')}
							image={<GoCheckCircle />}
							onClick={() => {
								setIsSupportVisible(user ? true : false)
							}}
							bgcolor={user ? colors.btnMainColor : colors.SecondGreyTextColor}
							color={colors.mainWhiteTextColor}
							border='none'
						/>
						{!user && tooltip === true && (
							<div className='tooltip-popup'>{t('listing.tooltip')}</div>
						)}
					</button>
				</>
			)}
		</>
	)
}
