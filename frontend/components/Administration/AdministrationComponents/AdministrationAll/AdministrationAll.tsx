import { IListingItem } from '@/components/Listing/Listing.interface'
import { MoreBtn } from '@/components/MoreBtn/MoreBtn'
import { Premium } from '@/components/SliderBlock/SliderBlock.style'
import { Pagination } from '@/components/ui/pagination'
import { getAllListings } from '@/services/administration'
import { delListing } from '@/services/getListings'
import { colors } from '@/utils'
import Image from "next/legacy/image"
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { FaRegTrashAlt } from 'react-icons/fa'
import { IoLocationOutline } from 'react-icons/io5'
import { PiMagnifyingGlass } from 'react-icons/pi'
import {
	ListingButtons,
	ListingInfo,
	ListingItem,
	ListingItems,
	ListingMain,
	ListingSpecial,
} from '../AdministrationListings/AdministrationListings.styled'

export const AdministrationAll = () => {
	const [listings, setListings] = useState<IListingItem[]>([])
	const [currentPage, setCurrentPage] = useState(1)
	const [totalPages, setTotalPages] = useState(1)

	const router = useRouter()
	const { t } = useTranslation()

	useEffect(() => {
		const fetchListings = async () => {
			try {
				const data = await getAllListings(currentPage)
				setListings(data.content)
				setTotalPages(data.totalPages || 1)
			} catch (error) {
				console.error(`${t('listingAll.errorFetchingListings')} ${error}`)
			}
		}

		fetchListings()
	}, [currentPage, t])

	const handleDelete = async (id: number) => {
		try {
			setListings(prevListings =>
				prevListings.filter(listing => listing.id !== id)
			)
			await delListing(id)
		} catch (error) {
			console.error('Error deleting listing:', error)
		}
	}

	return (
		<div>
			{listings.length > 0 ? (
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
													<Premium>{t('listingAll.top')}</Premium>
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
										onClick={() => handleDelete(listing.id)}
										color={colors.greyTextColor}
										bgcolor='transparent'
									>
										{t('listingAll.delete')}{' '}
										<FaRegTrashAlt fill={colors.errorColor} />
									</MoreBtn>
									<MoreBtn
										onClick={() => router.push(`/listing/${listing.id}`)}
									>
										{t('listingAll.view')} <PiMagnifyingGlass />
									</MoreBtn>
								</ListingButtons>
							</ListingItem>
						))}
					</ListingItems>

					{/* Pagination */}
					{totalPages > 1 && (
						<Pagination
							currentPage={currentPage}
							totalPages={totalPages}
							onPageChange={page => setCurrentPage(page)}
						/>
					)}
				</>
			) : (
				<p>{t('listingAll.noListingsFound')}</p>
			)}
		</div>
	)
}
