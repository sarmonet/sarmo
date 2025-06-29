'use client'

import { ListingSpecial } from '@/components/Administration/AdministrationComponents/AdministrationListings/AdministrationListings.styled'
import { DropDown } from '@/components/DropDown/DropDown'
import { useDevice } from '@/components/hooks/useDevice'
import { IListingItem } from '@/components/Listing/Listing.interface'
import ListingSearch from '@/components/ListingSearch/ListingSearch'
import { MoreBtn } from '@/components/MoreBtn/MoreBtn'
import { Favorite } from '@/components/SliderBlock/SliderBlock.style'
import { Pagination } from '@/components/ui/pagination'
import {
	delFavorite,
	getListingsByFilter,
	postFavorite,
} from '@/services/getListings'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from 'next/image'
import Link from 'next/link'
import { useRouter } from 'next/router'
import { useCallback, useEffect, useRef, useState } from 'react'
import { LiaFilterSolid } from 'react-icons/lia'
import { MdFavorite, MdFavoriteBorder } from 'react-icons/md'
import { CatalogSideBurger } from '../Catalog-data/CatalogItem/CatalogSideBurger'

import { useCatalog } from '../CatalogContext/CatalogContext'
import {
	CatalogHeader,
	CatalogHeaderFilter,
	CatalogItems,
	CatalogItemsTitle,
	CatalogListings,
	CatalogMainWrapper,
	ListingAbout,
	ListingBlock,
	ListingMain,
	ListingTop,
	NoListingsMessage,
	Premium,
} from './CatalogMain.styled'

export const CatalogMain = () => {
	const {
		activeCategory,
		selectedSubCategory,
		setFilteredListings,
		count,
		totalPages,
		currentPage,
		filteredListings,
		setCount,
		listings = [],
		loadingListings,
		errorListings,
		filters,
		favorite,
		updateFavorite,
		user,
		setCurrentPage,
		setFilteredParams,
	} = useCatalog()

	const router = useRouter()
	const [sortBy, setSortBy] = useState<string>('')
	const skipNextApplyRef = useRef(false)

	const [sortOrder, setSortOrder] = useState<string>('asc')
	const [hasInitialized, setHasInitialized] = useState(false)
	const [initializedFromOutside, setInitializedFromOutside] = useState(false)
	const [, setIsFiltered] = useState(false)
	const { isTablet, isMobile } = useDevice()
	const [isSideMenuOpen, setIsSideMenuOpen] = useState(false)
	const { t } = useTranslation('common')
	const handleSortChange = (value: string) => {
		let newSortBy = ''
		let newSortOrder = 'desc'

		if (value.includes('high')) newSortOrder = 'asc'

		if (value.includes('rating')) newSortBy = 'rating'
		else if (value.includes('views')) newSortBy = 'views'
		else if (value.includes('price')) newSortBy = 'price'
		else if (value.includes('createdAt')) newSortBy = 'createdAt'

		setSortBy(newSortBy)
		setSortOrder(newSortOrder)
	}

	const applyFilters = useCallback(async () => {
		try {
			if (activeCategory || selectedSubCategory || filters) {
				// eslint-disable-next-line @typescript-eslint/no-explicit-any
				const filterParams: any = {
					sortBy,
					sortOrder: 'desc',
					sqlFilters: {
						category: activeCategory?.id || null,
						subCategory: selectedSubCategory?.id || null,
						country: filters?.country || null,
						city: filters?.city || null,
						invest: filters?.invest || null,
						minPrice: filters?.minPrice || null,
						maxPrice: filters?.maxPrice || null,
					},
					mongoFilters: {},
					size: 12,
					page: currentPage - 1,
				}

				setFilteredParams(filterParams)

				if (filters) {
					for (const [key, value] of Object.entries(filters)) {
						if (!['country', 'city', 'minPrice', 'maxPrice'].includes(key)) {
							filterParams.mongoFilters[key] = value
						}
					}
				}

				const filteredData = await getListingsByFilter(filterParams)

				if (filteredData && filteredData.paginatedListings) {
					setFilteredListings(filteredData)
					setCount(filteredData.paginatedListings.totalElements)

					setIsFiltered(true)
				} else {
					setFilteredListings({ premiumListings: [], paginatedListings: [] })
					setCount(0)
					setIsFiltered(true)
				}
			} else {
				setFilteredListings({
					premiumListings: [],
					paginatedListings: listings,
				})
				setIsFiltered(false)
			}
		} catch (error) {
			console.error('Ошибка фильтрации:', error)
		}
	}, [
		activeCategory,
		selectedSubCategory,
		sortBy,
		sortOrder,
		filters,
		setFilteredListings,
		listings,
		setCount,
		setFilteredParams,
		currentPage,
	])

	const handleFavorite = async (listing: IListingItem) => {
		try {
			await postFavorite(listing.id)
			await updateFavorite()
		} catch (error) {
			console.error('Ошибка добавления в избранное:', error)
		}
	}

	const handleDeleteFavorite = async (listingId: number) => {
		try {
			await delFavorite(listingId)
			await updateFavorite()
		} catch (error) {
			console.error('Ошибка удаления из избранного:', error)
		}
	}
	const handlePageChange = (pageNumber: number) => {
		setCurrentPage(pageNumber)
		window.scrollTo(0, 0)
	}
	useEffect(() => {
		const hasExternalFilteredData =
			filteredListings?.paginatedListings?.content?.length > 0
		const externalInitFlag = router.query.externalInit === 'true'

		if (!hasInitialized) {
			if (externalInitFlag) {
				if (hasExternalFilteredData) {
					setCount(filteredListings?.paginatedListings.totalElements)
				} else {
					setCount(0)
				}
				skipNextApplyRef.current = true
				setInitializedFromOutside(true)
				setHasInitialized(true)
			} else {
				setHasInitialized(true)
			}
		}
	}, [filteredListings, router.query.externalInit, hasInitialized, setCount])

	useEffect(() => {
		if (initializedFromOutside) {
			setInitializedFromOutside(false)
		}
	}, [filters, activeCategory, selectedSubCategory])

	useEffect(() => {
		if (
			hasInitialized &&
			!initializedFromOutside &&
			!skipNextApplyRef.current
		) {
			applyFilters()
		} else {
		}
	}, [
		applyFilters,
		currentPage,
		activeCategory,
		selectedSubCategory,
		filters,
		hasInitialized,
		initializedFromOutside,
	])

	useEffect(() => {
		if (skipNextApplyRef.current && hasInitialized) {
			skipNextApplyRef.current = false
		}
	}, [filteredListings])

	const listingPush = (id: number) => {
		router.push(`/listing/${id}`)
	}

	if (loadingListings) return <p>Загрузка объявлений...</p>
	if (errorListings)
		return <p>Ошибка загрузки объявлений: {errorListings.message}</p>

	const renderListing = (listing: IListingItem, isPremium = false) => (
		<ListingBlock
			onClick={isTablet || isMobile ? () => listingPush(listing.id) : undefined}
			key={`${isPremium ? 'premium-' : 'listing-'}${listing.id}`}
		>
			{user &&
				(favorite?.some(fav => fav.id === listing.id) ? (
					<Favorite>
						<MdFavorite
							onClick={() => handleDeleteFavorite(listing.id)}
							color='red'
							size={24}
						/>
					</Favorite>
				) : (
					<Favorite onClick={() => handleFavorite(listing)}>
						<MdFavoriteBorder color='red' size={24} />
					</Favorite>
				))}

			<div className='image-container'>
				<ListingSpecial color={colors.btnSecondColor}>
					{listing.premiumSubscription === true && <Premium>топ</Premium>}
				</ListingSpecial>
				<Image
					src={listing.mainImage || '/images/sarmo.png'}
					alt={listing.title}
					width={256}
					height={256}
				/>
			</div>
			<ListingMain>
				<ListingTop>
					<h3>{listing.title}</h3>
					<p>{listing.category.name}</p>
					{/* <p >{listing.subCategory.name}</p> */}
					{listing.invest && (
						<p style={{ color: colors.btnSecondColor, marginLeft: '5px' }}>
							{t('buttons.investor')}
						</p>
					)}
				</ListingTop>
				<ListingAbout>
					<p>
						{listing.city}, {listing.country}
					</p>
					<span>{Number(listing.price).toLocaleString('de-DE')} $</span>
				</ListingAbout>

				<MoreBtn onClick={() => listingPush(listing.id)}>
					{t('buttons.more')}
				</MoreBtn>
			</ListingMain>
		</ListingBlock>
	)
	const displayPremium = filteredListings?.premiumListings || []
	const displayAllRaw =
		filteredListings?.paginatedListings?.content ?? listings ?? []
	const displayAll = Array.isArray(displayAllRaw) ? displayAllRaw : []
	const displayTotal = filteredListings?.paginatedListings.totalElements

	return (
		<CatalogMainWrapper>
			<CatalogHeader>
				<ListingSearch listings={listings} />

				<CatalogHeaderFilter>
					<div>
						<Link href='/'>{t('footerSections.main')}</Link> /{' '}
						<Link href='/catalog'>{t('menu.catalog')}</Link>
						{activeCategory && (
							<p>
								<span>/</span>
								{activeCategory.name}
							</p>
						)}
						{selectedSubCategory && (
							<p>
								<span>/</span>
								{selectedSubCategory.name}
							</p>
						)}
					</div>
					{activeCategory && (
						<DropDown
							isBorder
							width='220px'
							placeholder='Сортировать'
							isRounded
							options={[
								{ value: 'high rating', label: 'По большему рейтингу' },
								{ value: 'high views', label: 'По большей популярности' },
								{ value: 'low price', label: 'По большей стоимости' },
								{ value: 'high createdAt', label: 'Дата создания(поздние)' },
								{ value: 'low rating', label: 'По меньшей рейтингу' },
								{ value: 'low views', label: 'По меньшей популярности' },
								{ value: 'high price', label: 'По меньшей стоимости' },
								{ value: 'low createdAt', label: 'Дата создания(ранние)' },
							]}
							onChange={handleSortChange}
						/>
					)}
				</CatalogHeaderFilter>
			</CatalogHeader>

			<CatalogItems>
				{/* <CatalogItemsTitle>{count ? t('catalog.countListings') `${count}` : 'Нет объявлений'}</CatalogItemsTitle> */}

				<CatalogItemsTitle>
					{count
						? `${t('catalog.countListings')} ${count}`
						: t('catalog.noListings')}
				</CatalogItemsTitle>
				<>
					{(isMobile || isTablet) && (
						<div
							style={{
								display: 'flex',
								alignItems: 'center',
								position: 'relative',
								bottom: '-2px',
								left: isMobile ? '15%' : '14%',
								margin: '15px 0',
								transform: 'translateX(-50%)',
								backgroundColor: colors.btnMainColor,
								color: '#fff',
								padding: '12px 24px',
								borderRadius: '180px',
								cursor: 'pointer',
								width: 'fit-content',
								zIndex: 100,
							}}
							onClick={() => setIsSideMenuOpen(true)}
						>
							<LiaFilterSolid size={21} />
							Фильтры
						</div>
					)}
					<CatalogSideBurger
						isOpen={isSideMenuOpen}
						onClose={() => setIsSideMenuOpen(false)}
					/>
				</>
				<CatalogListings>
					{displayTotal === 0 ? (
						<NoListingsMessage>Объявлений не найдено</NoListingsMessage>
					) : (
						<>
							{displayPremium.length > 0
								? displayPremium.map((listing: IListingItem) =>
										renderListing(listing, true)
								  )
								: displayAll.map((listing: IListingItem) =>
										renderListing(listing)
								  )}
						</>
					)}
				</CatalogListings>
				{(filteredListings?.paginatedListings?.totalPages ||
					totalPages > 1) && (
					<Pagination
						currentPage={currentPage}
						totalPages={
							filteredListings?.paginatedListings?.totalPages || totalPages
						}
						onPageChange={handlePageChange}
					/>
				)}
			</CatalogItems>
		</CatalogMainWrapper>
	)
}
