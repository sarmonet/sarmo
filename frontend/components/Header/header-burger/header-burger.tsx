import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { DropDown } from '@/components/DropDown/DropDown'
import { useCheckAdmin } from '@/utils/useCheckAdmin'
import { useTranslation } from 'next-i18next'
import Image from "next/legacy/image"
import Link from 'next/link'
import { useRouter } from 'next/router'
import { FC, useEffect } from 'react'
import { FaRegUserCircle } from 'react-icons/fa'
import { IoChatbubbleOutline } from 'react-icons/io5'
import { MdFavoriteBorder } from 'react-icons/md'
import { RxHamburgerMenu } from 'react-icons/rx'
import { TbWorld } from 'react-icons/tb'
import { HeaderAction } from '../header-actions/headerActions.style'
import MenuItem from '../header-menu/menu-item/MenuItem'
import { getMenu } from '../header-menu/menu.data'
import {
	BurgerHeader,
	HeaderBurgerActions,
	HeaderBurgerBody,
	HeaderBurgerDrop,
	HeaderBurgerList,
	HeaderBurgerMain,
	HeaderBurgerWrapper,
} from './header-burger.styled'

interface HeaderBurgerProps {
	isOpen: boolean
	setIsOpen: (isOpen: boolean) => void
}

export const HeaderBurger: FC<HeaderBurgerProps> = ({ isOpen, setIsOpen }) => {
	const { user } = useCatalog()
	const { t } = useTranslation('common')
	const menu = getMenu(t)
	// const dropdownRef = useRef<HTMLDivElement>(null);
	// const router = useRouter();
	useEffect(() => {
		const handleRouteStart = () => {
			setIsOpen(false)
		}

		router.events.on('routeChangeStart', handleRouteStart)
		return () => {
			router.events.off('routeChangeStart', handleRouteStart)
		}
	}, [])

	const isAdmin = useCheckAdmin()
	const { setActiveTab } = useCatalog()
	const router = useRouter()
	if (isAdmin === null) return null
	const handleProfileListing = () => {
		if (!user) {
			router.push('/login')
			return
		}
		router.push('/profile')
		setActiveTab(2)
		setIsOpen(false)
	}
	const handleProfileSubscription = () => {
		if (!user) {
			setIsOpen(false)

			router.push('/login')
			return
		}
		setIsOpen(false)
		router.push('/profile')
	}

	const handleProfileFavorite = () => {
		if (!user) {
			router.push('/login')
			return
		}
		router.push('/profile')
		setActiveTab(1)
		setIsOpen(false)
	}

	const handleClose = () => {
		setIsOpen(false)
	}
	return (
		<HeaderBurgerWrapper>
			<button
				style={{ position: 'absolute', right: '2%', top: '45px' }}
				onClick={() => setIsOpen(true)}
			>
				<RxHamburgerMenu size={24} />
			</button>
			{isOpen && (
				<HeaderBurgerBody>
					{setIsOpen && (
						<button
							onClick={() => setIsOpen(false)}
							style={{
								position: 'absolute',
								top: '5%',
								right: '20px',
								fontSize: '24px',
							}}
						>
							X
						</button>
					)}
					<BurgerHeader>
						<HeaderAction onClick={handleProfileSubscription} isVip>
							{t('buttons.subscription')}
						</HeaderAction>

						<HeaderAction onClick={handleProfileListing}>
							{t('buttons.createListing')}
						</HeaderAction>
						<HeaderBurgerDrop>
							<DropDown
								value={<TbWorld size={23} />}
								options={[
									{ value: 'ru', label: 'ru' },
									{ value: 'uz', label: 'uz ' },
									{ value: 'en', label: 'en' },
								]}
								onChange={value => {
									router.push(router.pathname, router.asPath, { locale: value })
								}}
								width='fit-content'
							/>
						</HeaderBurgerDrop>
					</BurgerHeader>
					<HeaderBurgerMain>
						<HeaderBurgerActions>
							{/* <Link href="/login">
								Чат <IoChatbubbleOutline size={24} />
							</Link> */}

							<div
								style={{
									display: 'flex',
									alignItems: 'center',
									columnGap: '5px',
								}}
								onClick={() =>
									user ? handleProfileFavorite() : router.push('/login')
								}
							>
								Избранное <MdFavoriteBorder size={24} />
							</div>

							{user ? (
								<Link
									href='/profile'
									onClick={() => {
										handleClose()
									}}
								>
									Профиль{' '}
									{
										<Image
											src={user.profilePictureUrl || '/images/user/altUser.png'}
											alt='altUserLogo'
											width={32}
											height={32}
											style={{
												borderRadius: '50%',
												objectFit: 'cover',
												width: '32px',
												height: '32px',
											}}
										/>
									}
								</Link>
							) : (
								<Link
									href='/login'
									onClick={() => {
										handleClose()
									}}
								>
									Профиль <FaRegUserCircle size={24} />
								</Link>
							)}
							<Link href={user ? '/chat' : '/login'}>
								Чат <IoChatbubbleOutline size={23} />
							</Link>
						</HeaderBurgerActions>

						<HeaderBurgerList
							style={{
								display: 'flex',
								flexWrap: 'wrap',
								justifyContent: 'center',
								alignItems: 'center',
								gap: '20px',
							}}
						>
							{menu
								.filter(item => !item.adminOnly || isAdmin)
								.map(item => (
									<li
										key={item.link}
										onClick={() => {
											handleClose()
										}}
									>
										<MenuItem item={item} />
									</li>
								))}
							<div />
						</HeaderBurgerList>
					</HeaderBurgerMain>
				</HeaderBurgerBody>
			)}
		</HeaderBurgerWrapper>
	)
}
