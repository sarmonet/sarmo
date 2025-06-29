import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { DropDown as LanguageDropDown } from '@/components/DropDown/DropDown'
import { useDevice } from '@/components/hooks/useDevice'
import { ListingButton } from '@/components/Listing/ListingComponents/ListingButton'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from "next/legacy/image"
import Link from 'next/link'
import { useRouter } from 'next/router'
import { useEffect, useRef, useState } from 'react'
import { FaRegUserCircle } from 'react-icons/fa'
import { IoChatbubbleOutline } from 'react-icons/io5'
import { MdFavoriteBorder } from 'react-icons/md'
import { TbWorld } from 'react-icons/tb'
import { HeaderBurger } from '../header-burger/header-burger'
import {
	Actions,
	DropDown,
	HeaderAction,
	HeaderMain,
} from './headerActions.style'
export const HeaderActions = () => {
	const { t } = useTranslation('common')

	const { user } = useCatalog()
	const [isActive, setIsActive] = useState(false)
	const [isOpen, setIsOpen] = useState(false)
	const dropdownRef = useRef<HTMLDivElement>(null)
	const { isMobile, isTablet, isDesktop, isLargeTablet } = useDevice()

	const { setActiveTab } = useCatalog()
	const router = useRouter()

	useEffect(() => {
		const handleClickOutside = (event: MouseEvent) => {
			if (
				dropdownRef.current &&
				!dropdownRef.current.contains(event.target as Node)
			) {
				setIsActive(false)
			}
		}

		document.addEventListener('mousedown', handleClickOutside)
		return () => {
			document.removeEventListener('mousedown', handleClickOutside)
		}
	}, [])

	const handleIconClick = (event: React.MouseEvent) => {
		event.stopPropagation()
		setIsActive(!isActive)
	}
	const handleProfileListing = () => {
		if (!user) {
			router.push('/login')
			return
		}
		router.push('/profile')
		setActiveTab(2)
	}
	const handleProfileSubscription = () => {
		if (!user) {
			router.push('/login')
			return
		}
		router.push('/profile')
		setActiveTab(3)
	}

	const handleProfileFavorite = () => {
		if (!user) {
			router.push('/login')
			return
		}
		router.push('/profile')
		setActiveTab(1)
	}
	const handleProfileInfo = () => {
		if (!user) {
			router.push('/login')
			return
		}
		router.push('/profile')
		setActiveTab(0)
	}

	return (
		<Actions>
			{isDesktop && (
				<>
					<HeaderAction onClick={handleProfileSubscription} isVip>
						{t('buttons.subscription')}
					</HeaderAction>

					<HeaderAction onClick={handleProfileListing}>
						{t('buttons.createListing')}
					</HeaderAction>

					<HeaderMain>
						<LanguageDropDown
							value={<TbWorld size={23} />}
							bgc='transparent'
							width='fit-content'
							options={[
								{ value: 'ru', label: 'RU' },
								{ value: 'uz', label: 'UZ' },
								{ value: 'en', label: 'EN' },
							]}
							onChange={value => {
								router.push(router.pathname, router.asPath, { locale: value })
							}}
						/>

						<Link href={user ? '/chat' : '/login'}>
							<IoChatbubbleOutline size={23} />
						</Link>

						<div onClick={handleProfileFavorite}>
							<MdFavoriteBorder size={23} />
						</div>

						{user ? (
							<Link href='/profile'>
								<Image
									src={user.profilePictureUrl || '/images/user/altUser.png'}
									alt='ava'
									width={32}
									height={32}
									style={{
										borderRadius: '50%',
										objectFit: 'cover',
										width: '32px',
										height: '32px',
									}}
									onClick={handleProfileInfo}
								/>
							</Link>
						) : (
							<div ref={dropdownRef}>
								<div onClick={handleIconClick}>
									<FaRegUserCircle size={23} />
								</div>
								{isActive && (
									<DropDown>
										<ListingButton
											title='Вход'
											image={null}
											bgcolor={colors.btnMainColor}
											color={colors.mainWhiteTextColor}
											border='none'
											onClick={() => router.push('/login')}
										/>
										<ListingButton
											title='Регистрация'
											image={null}
											bgcolor='none'
											color={colors.SecondGreyTextColor}
											border={`1px solid ${colors.borderColor}`}
											onClick={() => router.push('/registration')}
										/>
									</DropDown>
								)}
							</div>
						)}
					</HeaderMain>
				</>
			)}

			{/* === БУРГЕР МЕНЮ ДЛЯ МОБИЛКИ === */}
			{(isMobile || isTablet || isLargeTablet) && (
				<div>
					<HeaderBurger setIsOpen={setIsOpen} isOpen={isOpen} />
				</div>
			)}
		</Actions>
	)
}
