import {
	delFavorite,
	getCategoryFields,
	getListingById,
	postFavorite,
} from '@/services/getListings'
import { useTranslation } from 'next-i18next'
import Link from 'next/link'
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { CiShare2 } from 'react-icons/ci'
import { IoMdEye } from 'react-icons/io'
import { IoLocationOutline } from 'react-icons/io5'
import { MdFavorite, MdFavoriteBorder } from 'react-icons/md'
import { useCatalog } from '../Catalog/CatalogContext/CatalogContext'
import ListingSearch from '../ListingSearch/ListingSearch'
import { ListingMap } from './ListingComponents/ListingMap/ListingMap'
// import { ISliderItem } from '../SliderBlock/SliderBlock'
import {
	FaFacebook,
	FaInstagram,
	FaLinkedin,
	FaTelegram,
	FaWhatsapp,
} from 'react-icons/fa'
import { Favorite } from '../SliderBlock/SliderBlock.style'
import { IListingItem } from './Listing.interface'
import {
	AnchorMenu,
	CountView,
	ListingBottomRating,
	ListingDetail,
	ListingDetails,
	ListingFilters,
	ListingLeft,
	ListingMain,
	ListingName,
	ListingNavigation,
	ListingRight,
	ListingRightBottom,
	ListingRightRating,
	ListingRightTitle,
	ListingRightTop,
	ListingRightTopActivity,
	ListingSection,
	ListingSubTitle,
	ListingWrapper,
	ShareStyle,
} from './Listing.styled'
import { formatDate } from './ListingComponents/FormatDateComponent'
import { ListingCommentaries } from './ListingComponents/ListingComentaries/ListingComentaries'
import { ListingGallery } from './ListingComponents/ListingGallery/ListingGallery'
import { ListingPresentation } from './ListingComponents/ListingPresintation/ListingPresintation'
import { ListingRightAbout } from './ListingComponents/ListingRightAbout/ListingRightAbout'
import { ListingSpecialistBlock } from './ListingComponents/ListingSpecialist/ListingSpecialist'
import { ListingSupportPop } from './ListingComponents/ListingSupport/ListingSupport'
import { RatingComponent } from './ListingComponents/RatingComponent'
import { RatingInput } from './ListingComponents/RatingInputComponent'
export const Listing = () => {
	const router = useRouter()
	const { id } = router.query
	const { t } = useTranslation('common')
	const [isHovered, setIsHovered] = useState(false)
	const { user, updateFavorite, favorite, listings } = useCatalog()
	const [listing, setListing] = useState<IListingItem | null>(null)
	const [, setFields] = useState<Record<string, unknown> | null>()
	// const [listings, setListings] = useState<ISliderItem[]>([])
	const [, setRating] = useState(0)
	const [, setUrl] = useState<string | null>(null)
	const [isSupportVisible, setIsSupportVisible] = useState(false)
	const [showShareOptions, setShowShareOptions] = useState(false)
	let createdAt = ''
	let updatedAt = ''

	if (listing) {
		createdAt = formatDate(listing.createdAt)
		updatedAt = formatDate(listing.updatedAt)
	} else {
		console.log('Listing is null')
	}

	const formatKey = (key: string): string => {
		return key
			.replace(/([A-Z])/g, ' $1')
			.replace(/^./, str => str.toUpperCase())
			.trim();
	}

	const DOCUMENT_FIELDS = ['Бизнес план', 'Презентация', 'Финансовая модель']

	const AnchorItems = (() => {
		const baseAnchors = [{ id: 'main-info', name: 'Основная информация' }]

		const fields = listing?.fields || {}

		const fieldAnchors = Object.entries(fields)
			.filter(([, value]) => {
				if (typeof value !== 'string') {
					return false
				}

				if (value.trim().length === 0) {
					return false
				}

				if (value.length <= 12) {
					return false
				}

				return true
			})
			.filter(([key]) => !DOCUMENT_FIELDS.includes(formatKey(key)))
			.map(([key]) => ({
				id: `field-${key}`,
				name: formatKey(key),
			}))

		const hasDocuments = Object.entries(fields).some(
			([key, value]) =>
				DOCUMENT_FIELDS.includes(formatKey(key)) &&
				typeof value === 'string' &&
				value.trim().length > 0
		)

		if (hasDocuments) {
			fieldAnchors.unshift({
				id: 'listing-documents',
				name: 'Документы',
			})
		}

		return [
			...baseAnchors,
			...fieldAnchors,
			{ id: 'support-chat', name: 'Комментарии' },
		]
	})()

	// const fetchListings = useCallback(async () => {
	// 	try {
	// 		const data = await getListings()
	// 		setListings(data)
	// 	} catch (error) {
	// 		console.error('Ошибка загрузки объявлений:', error)
	// 	}
	// }, [])

	// useEffect(() => {
	// 	fetchListings()
	// }, [fetchListings])

	useEffect(() => {
		if (!router.isReady) return
		if (!id || Array.isArray(id)) return

		const fetchListing = async () => {
			try {
				const numericId = parseInt(id as string, 10)
				if (isNaN(numericId)) return

				const data = await getListingById({ id: numericId })
				setListing(data)

				if (data?.category?.id) {
					const fieldsData = await getCategoryFields({ id: numericId })
					setFields(fieldsData)
				}
			} catch (error) {
				console.error('Ошибка загрузки объявления:', error)
			}
		}

		fetchListing()
	}, [router.isReady, id])

	const handleRatingChange = (newRating: number) => {
		setRating(newRating)
	}

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

	const currentURL = typeof window !== 'undefined' ? window.location.href : ''
	const shareText = 'Посмотрите это объявление:'
	const handleShareClick = () => {
		setShowShareOptions(prevState => !prevState)
	}

	const shareOnTelegram = () => {
		const telegramUrl = `https://t.me/share/url?url=${encodeURIComponent(
			currentURL
		)}&text=${encodeURIComponent(shareText)}`
		window.open(telegramUrl, '_blank')
		toast.success('Открываю Telegram для расшаривания!')
		setShowShareOptions(false)
	}

	const shareOnFacebook = () => {
		const facebookUrl = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(
			currentURL
		)}&quote=${encodeURIComponent(shareText)}`
		window.open(facebookUrl, '_blank')
		toast.success('Открываю Facebook для расшаривания!')
		setShowShareOptions(false)
	}

	const shareOnWhatsApp = () => {
		const whatsappUrl = `https://api.whatsapp.com/send?text=${encodeURIComponent(
			`${shareText} ${currentURL}`
		)}`
		window.open(whatsappUrl, '_blank')
		toast.success('Открываю WhatsApp для расшаривания!')
		setShowShareOptions(false)
	}

	const shareOnLinkedIn = () => {
		const linkedInUrl = `https://www.linkedin.com/sharing/share-offsite/?url=${encodeURIComponent(
			currentURL
		)}`
		window.open(linkedInUrl, '_blank')
		toast.success('Открываю LinkedIn для расшаривания!')
		setShowShareOptions(false)
	}

	const shareOnInstagram = () => {
		// Instagram не поддерживает веб-расшаривание напрямую
		navigator.clipboard
			.writeText(`${shareText} ${currentURL}`)
			.then(() => {
				window.open('https://www.instagram.com/', '_blank')
				toast.success('Ссылка скопирована! Вставьте её в Instagram вручную.')
				setShowShareOptions(false)
			})
			.catch(err => {
				console.error('Ошибка копирования ссылки для Instagram: ', err)
				toast.error('Не удалось скопировать ссылку для Instagram')
			})
	}

	const copyLinkToClipboard = () => {
		if (navigator.clipboard) {
			navigator.clipboard
				.writeText(currentURL)
				.then(() => {
					setUrl(currentURL)
					toast.success('Ссылка на листинг скопирована!')
					setShowShareOptions(false)
				})
				.catch(err => {
					console.error('Не удалось скопировать URL: ', err)
					toast.error('Не удалось скопировать ссылку')
				})
		}
	}

	if (!listing) return <p>Загрузка...</p>

	return (
		<ListingSection>
			<AnchorMenu>
				{AnchorItems.map(item => (
					<li key={item.id}>
						<button
							onClick={() => {
								const element = document.getElementById(item.id)
								if (element) {
									element.scrollIntoView({
										behavior: 'smooth',
										block: 'center',
									})
								}
							}}
						>
							{item.name}
						</button>
					</li>
				))}
			</AnchorMenu>

			<ListingSearch listings={listings} />
			<ListingNavigation>
				<li>
					<Link href='/'>{t('listing.navDefault1')}</Link>
				</li>
				<li>
					<Link href='/catalog'>{t('listing.navDefault2')}</Link>
				</li>
				<li>
					<Link
						href={{
							pathname: '/catalog',
							query: { categoryId: listing.category.id },
						}}
					>
						{listing.category.name}
					</Link>
				</li>
				<li>
					<Link
						href={{
							pathname: '/catalog',
							query: {
								categoryId: listing.category.id,
								subCategoryId: listing.subCategory.id,
							},
						}}
					>
						{listing.subCategory.name}
					</Link>
				</li>
			</ListingNavigation>
			<ListingWrapper>
				<ListingLeft>
					<ListingMain>
						<div
							style={{
								display: 'flex',
								alignItems: 'center',
								justifyContent: 'space-between',
							}}
						>
							<ListingName>{listing.title} </ListingName>{' '}
							<CountView>
								{listing.viewCount}
								<IoMdEye />
							</CountView>
						</div>
						<ListingSubTitle>
							<div
								style={{
									display: 'flex',
									alignItems: 'center',
									columnGap: '10px',
								}}
							>
								<IoLocationOutline /> <h4>{listing.city}</h4>
							</div>
							<span>{listing.premiumSubscription && 'топ'}</span>
						</ListingSubTitle>
						<ListingGallery
							title={listing.title}
							category={listing.category.name}
							mainImage={listing.mainImage}
							images={listing.images}
							videoUrl={listing.videoUrl}
						/>
						<ListingFilters>
							{' '}
							{Object.entries(listing.fields).map(([key, value]) => {
								if (typeof value === 'string' && value.slice(8, 12)) {
									return null
								}
								return (
									<button key={key}>
										<h3>
											{key}
											{key === 'Роялти' && '(%)'}:
										</h3>
										{Array.isArray(value) ? (
											<p>{value.join(', ')}</p>
										) : typeof value === 'number' ? (
											<p>{Math.floor(value)}</p>
										) : typeof value === 'boolean' ? (
											<p>{value ? 'Да' : 'Нет'}</p>
										) : (
											<p>{String(value)}</p>
										)}
									</button>
								)
							})}
						</ListingFilters>
						<ListingDetail id='main-info'>
							<h3>{t('listing.description')}</h3>
							<p>{listing.description}</p>
						</ListingDetail>
						<ListingDetails>
							{Object.entries(listing.fields).map(([key, value]) => {
								if (
									typeof value !== 'string' ||
									value.length < 9 ||
									value.includes('https://')
								) {
									return null
								}

								return (
									<ListingDetail key={key} id={`field-${key}`}>
										<h3>{key}</h3>
										<p>{value}</p>
									</ListingDetail>
								)
							})}
						</ListingDetails>
					</ListingMain>
				</ListingLeft>
				<ListingRight>
					<ListingRightTop>
						<ListingRightTitle>
							<h2>
								{!listing.invest ? t('listing.price') : t('listing.budget')}
							</h2>
							<span>{Number(listing.price).toLocaleString('de-DE')} $</span>
						</ListingRightTitle>
						<ListingRightTopActivity
							style={{ display: 'flex', columnGap: '20px' }}
						>
							<div style={{ position: 'relative', display: 'inline-block' }}>
								<div
									onClick={handleShareClick}
									className='cursor-pointer hover:scale-125 transition-transform duration-300 '
								>
									<CiShare2 size={24} />
								</div>

								{showShareOptions && (
									<div
										style={{
											position: 'absolute',
											top: '200%',
											left: '-200%',
											backgroundColor: 'white',
											border: '1px solid #ccc',
											borderRadius: '4px',
											padding: '8px',
											zIndex: 100,
											display: 'flex',
											flexDirection: 'column',
											gap: '12px',
											marginTop: '5px',
										}}
									>
										<ShareStyle onClick={shareOnTelegram}>
											<FaTelegram size={20} color='#0088cc' /> Telegram
										</ShareStyle>

										<ShareStyle onClick={shareOnFacebook}>
											<FaFacebook size={20} color='#1877f2' /> Facebook
										</ShareStyle>

										<ShareStyle onClick={shareOnWhatsApp}>
											<FaWhatsapp size={20} color='#25D366' /> WhatsApp
										</ShareStyle>

										<ShareStyle onClick={shareOnLinkedIn}>
											<FaLinkedin size={20} color='#0077b5' /> LinkedIn
										</ShareStyle>

										<ShareStyle onClick={shareOnInstagram}>
											<FaInstagram size={20} color='#E4405F' /> Instagram
										</ShareStyle>

										<ShareStyle onClick={copyLinkToClipboard}>
											<CiShare2 size={20} /> Копировать ссылку
										</ShareStyle>
									</div>
								)}
							</div>
							{user && (
								<Favorite
									style={{ position: 'relative' }}
									onMouseEnter={() => setIsHovered(true)}
									onMouseLeave={() => setIsHovered(false)}
									onClick={() => {
										if (favorite?.some(fav => fav.id === listing.id)) {
											handleDeleteFavorite(listing.id)
										} else {
											handleFavorite(listing)
										}
									}}
								>
									{isHovered ? (
										favorite?.some(fav => fav.id === listing.id) ? (
											<MdFavorite color='red' size={24} />
										) : (
											<MdFavoriteBorder color='red' size={24} />
										)
									) : favorite?.some(fav => fav.id === listing.id) ? (
										<MdFavorite color='red' size={24} />
									) : (
										<MdFavoriteBorder color='red' size={24} />
									)}
								</Favorite>
							)}
						</ListingRightTopActivity>
					</ListingRightTop>
					<ListingRightRating>
						<div>
							{t('listing.rating')}
							<span>{listing.totalRating ? listing.totalRating : '0'}</span>
						</div>
						<p>
							{listing.averageRating ? listing.averageRating.toFixed(1) : '0.0'}
							<RatingComponent
								rating={listing.averageRating ? listing.averageRating : 0}
							/>
						</p>
					</ListingRightRating>
					<ListingRightAbout
						listingId={listing.id}
						investor={listing.invest}
						createdAt={createdAt}
						updatedAt={updatedAt}
						setIsSupportVisible={setIsSupportVisible}
					/>
					<ListingRightBottom>
						<ListingSpecialistBlock
							id={listing.user.id}
							firstName={listing.user.firstName}
							email={listing.user.email}
							lastName={listing.user.lastName}
							profileImageUrl={listing.user.profileImageUrl}
							phoneNumber={listing.user.phoneNumber}
						/>
						<ListingMap
							city={listing.city}
							country={listing.country}
							fullAddress={listing.fullAddress}
						/>
						<div id='listing-documents'>
							<ListingPresentation listingPresintation={listing.fields} />
						</div>
					</ListingRightBottom>
				</ListingRight>
			</ListingWrapper>

			<ListingBottomRating>
				<div>
					{t('listing.rating')}
					<span>{listing.totalRating ? listing.totalRating : '0'}</span>
				</div>
				{user?.id && listing.id && (
					<div>
						<h4 style={{ marginRight: '12px' }}>
							{listing.averageRating ? listing.averageRating.toFixed(1) : '0.0'}
						</h4>
						<RatingInput
							listingId={listing.id}
							userId={user.id}
							onRatingChange={handleRatingChange}
						/>
					</div>
				)}
			</ListingBottomRating>
			<div id='support-chat'>
				<ListingCommentaries listingId={listing.id} />
			</div>
			{/* <ListingSimilar similarListings = {listing.similarListings}/> */}
			{isSupportVisible ? (
				<ListingSupportPop
					setIsSupportVisible={setIsSupportVisible}
					listingId={listing.id}
				/>
			) : null}
		</ListingSection>
	)
}
