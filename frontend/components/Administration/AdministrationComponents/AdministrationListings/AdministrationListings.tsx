import { Premium } from '@/components/Catalog/CatalogMain/CatalogMain.styled'
import { IListingItem } from '@/components/Listing/Listing.interface'
import { MoreBtn } from '@/components/MoreBtn/MoreBtn'
import { Pagination } from '@/components/ui/pagination'
import { getInactive, putStatus } from '@/services/getListings'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from "next/legacy/image"
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { IoLocationOutline } from 'react-icons/io5'
import { MdOutlineVerified } from 'react-icons/md'
import { PiMagnifyingGlass } from 'react-icons/pi'
import { RxValueNone } from 'react-icons/rx'
import {
	ListingButtons,
	ListingInfo,
	ListingItem,
	ListingItems,
	ListingMain,
	ListingSpecial,
} from './AdministrationListings.styled'
export const AdministrationListings = () => {
	const [listings, setListings] = useState<IListingItem[]>([])
	const [currentPage, setCurrentPage] = useState(1)
	const [totalPages, setTotalPages] = useState(1)
	const { t } = useTranslation('common')
	const router = useRouter()

	const fetchInactive = async (page: number) => {
		try {
			const response = await getInactive(page)
			setListings(response.content)
			setTotalPages(response.totalPages || 1)
		} catch (error) {
			console.error('Error fetching inactive listings:', error)
		}
	}

	useEffect(() => {
		fetchInactive(currentPage)
	}, [currentPage])

	const handleStatus = async (id: number, status: string) => {
		try {
			await putStatus({ id }, status)
			setListings(prevListings =>
				prevListings.filter(listing => listing.id !== id)
			)
		} catch (error) {
			console.error('Error updating listing:', error)
		}
	}

	return (
		<div>
			{listings?.length > 0 ? (
				<>
					<ListingItems>
						{listings.map(listing => (
							<ListingItem key={listing.id}>
								<ListingMain>
									<Image
										src={listing.mainImage || '/images/sarmo.png'}
										alt='listingImg'
										width={200}
										height={200}
									/>
									<ListingInfo>
										<h2>{listing.title}</h2>
										<div style={{ display: 'flex', alignItems: 'center' }}>
											<span
												style={{
													display: 'flex',
													alignItems: 'center',
													columnGap: '5px',
												}}
											>
												<IoLocationOutline />
												{listing.city}
											</span>
											<ListingSpecial color={colors.btnSecondColor}>
												{listing.premiumSubscription === true && (
													<Premium>{t('listingInavtive.top')}</Premium>
												)}
											</ListingSpecial>
											<ListingSpecial color={colors.btnMainColor}>
												{listing.category.name}
											</ListingSpecial>
										</div>
										<h3>{Number(listing.price).toLocaleString('de-DE')} $</h3>
									</ListingInfo>
								</ListingMain>
								<ListingButtons>
									<MoreBtn
										onClick={() => handleStatus(listing.id, 'REJECTED')}
										color={colors.greyTextColor}
										bgcolor='transparent'
									>
										{t('listingInavtive.reject')} <RxValueNone />
									</MoreBtn>
									<MoreBtn onClick={() => handleStatus(listing.id, 'ACTIVE')}>
										{t('listingInavtive.verify')} <MdOutlineVerified />
									</MoreBtn>
									<MoreBtn
										onClick={() => router.push(`/listing/${listing.id}`)}
									>
										{t('listingInavtive.view')} <PiMagnifyingGlass />
									</MoreBtn>
								</ListingButtons>
							</ListingItem>
						))}
					</ListingItems>

					{totalPages > 1 && (
						<Pagination
							currentPage={currentPage}
							totalPages={totalPages}
							onPageChange={page => setCurrentPage(page)}
						/>
					)}
				</>
			) : (
				<p>{t('listingInavtive.noInactive')}</p>
			)}
		</div>
	)
}
