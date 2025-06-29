import {
	Favorite,
	ListingHorizontalAbout,
	ListingHorizontalBlock,
	ListingSpecial,
	ListingTop,
	Premium,
} from '@/components/Catalog/CatalogMain/CatalogMain.styled'
import { IListingItem } from '@/components/Listing/Listing.interface'
import { MoreBtn } from '@/components/MoreBtn/MoreBtn'
import { Title } from '@/components/Title/Title'
import { useDevice } from '@/components/hooks/useDevice'
import { delListing, getUserListings } from '@/services/getListings'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from "next/legacy/image"
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { FaRegTrashAlt } from 'react-icons/fa'
import { EditingListing } from './EditingListing'
export const ListingMyListings = () => {
	const [myListings, setMyListings] = useState<IListingItem[]>([])
	const [deletePopupId, setDeletePopupId] = useState<number | null>(null)
	const [editingListingId, setEditingListingId] = useState<number | null>(null)
	const { isTablet, isMobile } = useDevice()
	const router = useRouter()
	const { t } = useTranslation('common')
	const listingPush = (id: number) => {
		router.push(`/listing/${id}`)
	}

	const handleEditClick = (id: number) => {
		setEditingListingId(id)
	}

	const handleDelete = async (id: number) => {
		try {
			setMyListings(prevListings =>
				prevListings.filter(listing => listing.id !== id)
			)
			await delListing(id)
			setDeletePopupId(null)
		} catch (error) {
			console.error('Error deleting listing:', error)
		}
	}

	const fetchListings = async () => {
		try {
			const response = await getUserListings()
			setMyListings(response)
		} catch (error) {
			console.error('Error fetching listings:', error)
		}
	}

	useEffect(() => {
		fetchListings()
	}, [])

	if (editingListingId !== null) {
		return (
			<EditingListing
				listingId={editingListingId}
				onClose={() => {
					setEditingListingId(null)
					fetchListings()
				}}
			/>
		)
	}

	return (
		<>
			<Title>{t('profileListingsAside.listingsMine')}</Title>
			<>
				<div>
					{myListings.length > 0 ? (
						myListings.map(item => (
							<ListingHorizontalBlock key={item.id} className='notHide'>
								<Favorite>
									<FaRegTrashAlt
										onClick={() => setDeletePopupId(item.id)}
										color='red'
										size={24}
									/>
								</Favorite>

								<div
									className='image-container'
									onClick={
										isTablet || isMobile
											? () => listingPush(item.id)
											: undefined
									}
								>
									{item.premiumSubscription && (
										<ListingSpecial color={colors.btnSecondColor}>
											<Premium>топ</Premium>
										</ListingSpecial>
									)}
									<Image
										src={item.mainImage || '/images/slider/1.jpg'}
										alt={item.title || 'Без названия'}
										width={156}
										height={156}
									/>
								</div>
								<ListingHorizontalAbout>
									<ListingTop>
										<h3>{item.title || 'Название отсутствует'}</h3>
										<div className='flex flex-col gap-[10px] mt-[10px] '>
											<p>{item.category?.name || 'Категория не указана'}</p>

											{item.invest && (
												<p
													style={{
														color: colors.btnSecondColor,
														marginLeft: '5px',
													}}
												>
													{t('buttons.investor')}
												</p>
											)}
										</div>
									</ListingTop>
								</ListingHorizontalAbout>
								<div className='flex flex-wrap gap-[20px]'>
									<MoreBtn onClick={() => listingPush(item.id)}>
										{t('buttons.more')}
									</MoreBtn>
									<MoreBtn onClick={() => handleEditClick(item.id)}>
										{t('profileListingsContent.changeListing')}
									</MoreBtn>
								</div>
							</ListingHorizontalBlock>
						))
					) : (
						<p>{t('profileListingsContent.noContent')}</p>
					)}
				</div>
			</>

			{deletePopupId !== null && (
				<div
					style={{
						position: 'fixed',
						top: 0,
						left: 0,
						width: '100vw',
						height: '100vh',
						backgroundColor: 'rgba(0, 0, 0, 0.5)',
						display: 'flex',
						justifyContent: 'center',
						alignItems: 'center',
						zIndex: 1001,
					}}
				>
					<div
						style={{
							display: 'flex',
							flexDirection: 'column',
							gap: '16px',
							backgroundColor: colors.mainWhiteTextColor,
							padding: '24px',
							borderRadius: '12px',
							boxShadow: '0 4px 20px rgba(0,0,0,0.2)',
							width: '300px',
							textAlign: 'center',
						}}
					>
						<h2 style={{ color: colors.errorColor }}>
							{t('profileListingsContent.deleteListing')}
						</h2>
						<p>{t('profileListingsContent.deleteDescription')}</p>
						<div
							style={{
								display: 'flex',
								justifyContent: 'space-around',
								color: colors.mainWhiteTextColor,
							}}
						>
							<button
								style={{
									backgroundColor: colors.btnMainColor,
									padding: '8px 28px',
									borderRadius: '6px',
								}}
								onClick={() => handleDelete(deletePopupId)}
							>
								{t('buttons.yes')}
							</button>
							<button
								style={{
									backgroundColor: colors.btnSecondColor,
									padding: '8px 28px',
									borderRadius: '6px',
								}}
								onClick={() => setDeletePopupId(null)}
							>
								{t('buttons.no')}
							</button>
						</div>
					</div>
				</div>
			)}
		</>
	)
}
