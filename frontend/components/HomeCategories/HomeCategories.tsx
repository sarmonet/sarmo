'use client'
import { getSubCategories } from '@/services/getCategories'
import { getCountByCategoryId, getTotalCount } from '@/services/getListings'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from "next/legacy/image"
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { ICatalogSub } from '../Catalog/Catalog-data/CatalogInterface/Catalog.interface'
import { useCatalog } from '../Catalog/CatalogContext/CatalogContext'
import { Title } from '../Title/Title'
import {
	HomeCategoriesContent,
	HomeCategoriesFilter,
	HomeCategoriesWrapper,
	MobileFilterOverlay,
	MobileFilterPopup,
} from './HomeCategories.style'

export const HomeCategories = () => {
	const [activeId, setActiveId] = useState<number | null>(null)
	const [activeSubCategories, setActiveSubCategories] = useState<ICatalogSub[]>(
		[]
	)
	const [isMobileFilterOpen, setIsMobileFilterOpen] = useState(false)
	const { t } = useTranslation('common')
	const {
		categories,
		activeCategory,
		setActiveCategory,
		count,
		setCount,
		setSelectedSubCategory,
	} = useCatalog()
	const router = useRouter()

	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	const handleCategoryClick = (category: any) => {
		if (activeId === category.id) {
			setActiveId(null)
			setActiveCategory(null)
			setActiveSubCategories([])
			setIsMobileFilterOpen(false)
		} else {
			setActiveId(category.id)
			setActiveCategory(category)
			if (window.innerWidth <= 768) {
				setIsMobileFilterOpen(true)
			}
		}
	}

	useEffect(() => {
		if (activeId) {
			const fetchSubCategories = async () => {
				try {
					const data = await getSubCategories({ id: activeId })
					if (Array.isArray(data)) {
						setActiveSubCategories(data)
					} else {
						setActiveSubCategories([])
						console.error('❌ API вернул не массив подкатегорий', data)
					}
				} catch (error) {
					console.error('Ошибка загрузки подкатегорий:', error)
					setActiveSubCategories([])
				}
			}
			fetchSubCategories()
		} else {
			setActiveSubCategories([])
			setIsMobileFilterOpen(false)
		}

		const fetchCount = async () => {
			try {
				let data
				if (activeCategory) {
					data = await getCountByCategoryId({ id: activeCategory.id })
				} else {
					data = await getTotalCount()
				}
				setCount(data)
			} catch (error) {
				console.error('Ошибка загрузки количества объявлений:', error)
			}
		}

		fetchCount()
	}, [activeId, activeCategory, setCount])

	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	const handleNavigateToCategory = (category: any) => {
		setActiveCategory(category)
		setSelectedSubCategory(null)
		router.push({
			pathname: '/catalog',
			query: { categoryId: category.id },
		})
		setIsMobileFilterOpen(false)
	}

	const handleSubCategoryClick = (item: ICatalogSub) => {
		const category = categories.find(cat => cat.id === activeCategory?.id)
		if (category) {
			setActiveCategory(category)
			setSelectedSubCategory(item)
			setIsMobileFilterOpen(false)
			router.push({
				pathname: '/catalog',
				query: { categoryId: category.id, subCategoryId: item.id },
			})
		}
	}

	const closeMobileFilter = () => {
		setIsMobileFilterOpen(false)
		setActiveId(null)
		setActiveCategory(null)
		setActiveSubCategories([])
	}

	return (
		<HomeCategoriesWrapper>
			<Title>{t('heroTitles.categories')}</Title>
			<HomeCategoriesContent>
				{categories.map(category => {
					const isActive = activeId === category.id
					return (
						<div
							key={category.id}
							onClick={() => handleCategoryClick(category)}
							style={{
								transform: isActive ? 'scale(1.6)' : 'initial',
								transition: '0.3s ease',
								cursor: 'pointer',
							}}
						>
							<Image
								src={category.imageUrl || '/images/categories/1.png'}
								alt={category.name}
								width={140}
								height={140}
								className='min-w-[140px] min-h-[140px] max-w-[140px] max-h-[140px]  '
							/>
							<p style={{ color: `${colors.mainTextColor}` }}>
								{category.name}
							</p>
						</div>
					)
				})}
			</HomeCategoriesContent>

			{activeSubCategories.length > 0 &&
				activeCategory &&
				window.innerWidth > 768 && (
					<HomeCategoriesFilter>
						<h3
							onClick={() =>
								activeCategory && handleNavigateToCategory(activeCategory)
							}
							style={{ cursor: 'pointer' }}
						>
							{activeCategory.name} <span>({count})</span>
						</h3>
						<ul>
							{activeSubCategories.map(item => (
								<li key={item.id} onClick={() => handleSubCategoryClick(item)}>
									<span>{item.name}</span>
								</li>
							))}
						</ul>
					</HomeCategoriesFilter>
				)}

			{isMobileFilterOpen &&
				activeSubCategories.length > 0 &&
				activeCategory && (
					<>
						<MobileFilterOverlay onClick={closeMobileFilter} />
						<MobileFilterPopup>
							<h3
								onClick={() =>
									activeCategory && handleNavigateToCategory(activeCategory)
								}
							>
								{activeCategory.name} <span>({count})</span>
							</h3>
							<ul>
								{activeSubCategories.map(item => (
									<li
										key={item.id}
										onClick={() => handleSubCategoryClick(item)}
									>
										<span>{item.name}</span>
									</li>
								))}
							</ul>
							<button onClick={closeMobileFilter}>X</button>
						</MobileFilterPopup>
					</>
				)}
		</HomeCategoriesWrapper>
	)
}
