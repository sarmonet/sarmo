'use client'

import { delFavorite, postFavorite } from '@/services/getListings'
import { colors } from '@/utils'
import Image from "next/legacy/image"
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { MdFavorite, MdFavoriteBorder } from 'react-icons/md'
import Slider from 'react-slick'
import 'slick-carousel/slick/slick-theme.css'
import 'slick-carousel/slick/slick.css'
import { useCatalog } from '../Catalog/CatalogContext/CatalogContext'
import { IListingItem } from '../Listing/Listing.interface'
import {
	Favorite,
	StyledAbout,
	StyledImageWrapper,
	StyledInvestorBadge,
	StyledSlide,
	StyledSlideContentWrapper,
	StyledSliderContainer,
	StyledTop,
} from './SliderBlock.style'

export interface ISliderItem {
	id: number
	image?: string
	title: string
	category: { id: number; name: string }
	city: string
	mainImage: string
	country: string
	price: number
	invest: boolean
}

interface SliderBlockProps {
	listings: ISliderItem[]
}

export const SliderBlock: React.FC<SliderBlockProps> = ({ listings }) => {
	const router = useRouter()
	const [isMounted, setIsMounted] = useState(false)
	const { user, favorite, updateFavorite } = useCatalog()
	useEffect(() => {
		setIsMounted(true)
	}, [])

	if (!isMounted) return null
	if (!listings || listings.length === 0)
		return <p>Нет данных для отображения.</p>
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
	const settings = {
		dots: false,
		infinite: listings.length > 2,
		speed: 500,
		slidesToShow: 4,
		slidesToScroll: 1,
		draggable: true,
		swipeToSlide: true,
		arrows: false,
		responsive: [
			{ breakpoint: 1440, settings: { slidesToShow: 4 } },
			{ breakpoint: 1024, settings: { slidesToShow: 3 } },
			{ breakpoint: 768, settings: { slidesToShow: 2 } },
			{ breakpoint: 480, settings: { slidesToShow: 2 } },
		],
	}

	return (
		<StyledSliderContainer>
			<Slider {...settings}>
				{listings.map(slide => (
					<StyledSlide key={slide.id}>
						{user &&
							(favorite?.some(fav => fav.id === slide.id) ? (
								<Favorite>
									<MdFavorite
										onClick={() => handleDeleteFavorite(slide.id)}
										color='red'
										size={24}
									/>
								</Favorite>
							) : (
								<Favorite onClick={() => handleFavorite(slide as IListingItem)}>
									<MdFavoriteBorder color='red' size={24} />
								</Favorite>
							))}

						<StyledSlideContentWrapper
							onClick={() => router.push(`/listing/${slide.id}`)}
						>
							<StyledImageWrapper>
								{slide.image || slide.mainImage ? (
									<Image
										src={slide.image || slide.mainImage}
										alt={slide.title || 'Listing image'}
										layout='fill'
										objectFit='cover'
									/>
								) : (
									<Image
										src={'/images/sarmo.png'}
										alt={'Sarmo image'}
										layout='fill'
										objectFit='cover'
									/>
								)}
							</StyledImageWrapper>

							<div style={{ padding: '0 10px' }}>
								<StyledTop>
									<h3>{slide.title}</h3>
									<p>{slide.category.name}</p>
									{slide.invest && (
										<StyledInvestorBadge
											style={{ color: colors.btnSecondColor }}
										>
											От инвестора
										</StyledInvestorBadge>
									)}
								</StyledTop>
								<StyledAbout>
									<p>
										{slide.city}, {slide.country}
									</p>
									<span>{Number(slide.price).toLocaleString('de-DE')} $</span>
								</StyledAbout>
							</div>
						</StyledSlideContentWrapper>
					</StyledSlide>
				))}
			</Slider>
		</StyledSliderContainer>
	)
}
