import { useDevice } from '@/components/hooks/useDevice'
import { getSubCategories } from '@/services/getCategories'
import {
	Filters,
	getCategoryMongoFilter,
	getFieldsById,
	useApplyFilters,
} from '@/services/getListings'
import { colors } from '@/utils'
import router from 'next/router'
import { useEffect, useState } from 'react'
import { CiFilter } from 'react-icons/ci'
import { MdKeyboardArrowRight } from 'react-icons/md'
import { useCatalog } from '../../CatalogContext/CatalogContext'
import { CatalogFilter } from '../../CatalogFilter/CatalogFilter'
import { ICatalogSub } from '../CatalogInterface/Catalog.interface'
import {
	CatalogHiddenList,
	CatalogItemList,
	CatalogItemWrapper,
	CatalogSubList,
	CatalogSubWrapper,
	CatalogTitle,
	SelectedSubCategory,
} from './CatalogItem.styled'

export type RecentSearch = {
	category: { id: number; name: string; imageUrl: string }
	subCategory: { id: number; name: string } | null
}

export const CatalogItem = () => {
	const {
		categories,
		subCategories,
		activeCategory,
		selectedSubCategory,
		setSubCategories,
		setFields,
		setCurrentPage,
		setFilteredListings,
		setActiveCategory,
		setSelectedSubCategory,
		setMongo,
		setFilters,
		filters,
	} = useCatalog()

	const [, setLoading] = useState(false)
	const [additionalFilters, setAdditionalFilters] = useState<
		Record<string, unknown>
	>({})
	const { isDesktop } = useDevice()

	const allFilters: Filters = {
		...(filters as Filters),
		...additionalFilters,
	}
	const MAX_RECENT = 3

	const saveRecentSearch = (newSearch: RecentSearch) => {
		const stored = localStorage.getItem('recentCategories')
		let searches: RecentSearch[] = []

		if (stored) {
			try {
				searches = JSON.parse(stored)
			} catch {
				console.warn('Ошибка парсинга recentCategories')
			}
		}

		searches = searches.filter(
			item =>
				item.category.id !== newSearch.category.id ||
				item.subCategory?.id !== newSearch.subCategory?.id
		)

		searches.unshift(newSearch)

		if (searches.length > MAX_RECENT) {
			searches = searches.slice(0, MAX_RECENT)
		}

		localStorage.setItem('recentCategories', JSON.stringify(searches))
	}

	const applyFilters = useApplyFilters(
		activeCategory,
		selectedSubCategory,
		allFilters,
		setFilteredListings,
		setLoading
	)
	useEffect(() => {
		const { query } = router
		const categoryIdFromUrl = query.category
			? parseInt(query.category as string, 10)
			: undefined
		const subCategoryIdFromUrl = query.subCategory
			? parseInt(query.subCategory as string, 10)
			: undefined
		const sortByFromUrl = query.sortBy as string | undefined
		const sortOrderFromUrl = query.sortOrder as string | undefined
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		const newSqlFilters: Record<string, any> = {}
		if (query.minPrice)
			newSqlFilters.minPrice = parseInt(query.minPrice as string, 10)
		if (query.maxPrice)
			newSqlFilters.maxPrice = parseInt(query.maxPrice as string, 10)
		if (query.country) newSqlFilters.country = query.country as string
		if (query.city) newSqlFilters.city = query.city as string
		if (query.invest) newSqlFilters.invest = query.invest as string

		const newFilters = { ...(filters ?? {}) }

		if (sortByFromUrl) newFilters.sortBy = sortByFromUrl
		if (sortOrderFromUrl) newFilters.sortOrder = sortOrderFromUrl
		newFilters.sqlFilters = {
			...(newFilters.sqlFilters ?? {}),
			...newSqlFilters,
		}
		setFilters(newFilters)

		const newMongoFilters: Record<string, string> = {}
		for (const key in query) {
			if (
				key !== 'category' &&
				key !== 'subCategory' &&
				key !== 'sortBy' &&
				key !== 'sortOrder' &&
				key !== 'minPrice' &&
				key !== 'maxPrice' &&
				key !== 'country' &&
				key !== 'city' &&
				key !== 'invest'
			) {
				newMongoFilters[key] = query[key] as string
			}
		}
		setAdditionalFilters(newMongoFilters)

		if (categoryIdFromUrl) {
			const category = categories.find(cat => cat.id === categoryIdFromUrl)
			if (category) {
				setActiveCategory(category)
			}
		}

		if (subCategoryIdFromUrl) {
			const subCategory = subCategories.find(
				sub => sub.id === subCategoryIdFromUrl
			)
			if (subCategory) {
				setSelectedSubCategory(subCategory)
			}
		}

		applyFilters()
	}, [
		router.query,
		setActiveCategory,
		setSelectedSubCategory,
		setAdditionalFilters,
		applyFilters,
		categories,
		setFilters,
		subCategories,
	])

	useEffect(() => {
		if (activeCategory) {
			const fetchData = async () => {
				try {
					const [subs, mongoFilters, fieldsData] = await Promise.all([
						getSubCategories({ id: activeCategory.id }),
						getCategoryMongoFilter({ id: activeCategory.id }),
						getFieldsById({ id: activeCategory.id }),
						setCurrentPage(1),
					])

					setMongo(mongoFilters || { fields: {} })
					setSubCategories(Array.isArray(subs) ? subs : [])
					setFields(fieldsData || {})
				} catch (error) {
					console.error('Ошибка загрузки данных:', error)
				}
			}

			fetchData()
		}
	}, [activeCategory, setSubCategories, setFields, setMongo, setCurrentPage])

	useEffect(() => {
		applyFilters()
	}, [applyFilters])

	const handleSubCategoryClick = (subItem: ICatalogSub) => {
		setSelectedSubCategory(subItem)
		if (activeCategory) {
			saveRecentSearch({
				category: {
					id: activeCategory.id,
					name: activeCategory.name,
					imageUrl: activeCategory.imageUrl,
				},
				subCategory: { id: subItem.id, name: subItem.name },
			})
		}
	}

	return (
		<>
			{isDesktop && (
				<CatalogItemWrapper>
					<CatalogItemList>
						{categories.map(category => (
							<li key={category.id}>
								{!activeCategory ? (
									<span onClick={() => setActiveCategory(category)}>
										{category.name}
									</span>
								) : (
									activeCategory.id === category.id && (
										<CatalogHiddenList>
											{selectedSubCategory && (
												<div
													style={{
														color: colors.darkBlueBgColor,
														fontSize: '32px',
														fontWeight: '500',
														display: 'flex',
														alignItems: 'center',
														columnGap: '10px',
													}}
												>
													<CiFilter color={colors.btnMainColor} />
													<div>Фильтры</div>
												</div>
											)}

											<CatalogTitle
												onClick={() => {
													setActiveCategory(null)
													setSelectedSubCategory(null)
													setFilteredListings(null)
													setCurrentPage(1)
												}}
												isActive={true}
												isFilter={!!selectedSubCategory}
											>
												<span>{category.name}</span>
											</CatalogTitle>

											<CatalogSubWrapper>
												<CatalogSubList>
													{!selectedSubCategory ? (
														subCategories.length > 0 ? (
															subCategories.map(subItem => (
																<span
																	key={subItem.id}
																	onClick={() =>
																		handleSubCategoryClick(subItem)
																	}
																>
																	{subItem.name}
																</span>
															))
														) : (
															<p>Нет подкатегорий</p>
														)
													) : (
														<SelectedSubCategory
															onClick={() => {
																setSelectedSubCategory(null)
																setFilteredListings(null)
															}}
														>
															{selectedSubCategory.name}
															<MdKeyboardArrowRight />
														</SelectedSubCategory>
													)}
												</CatalogSubList>
											</CatalogSubWrapper>

											{selectedSubCategory && (
												<CatalogFilter
													categoryId={activeCategory.id}
													onFilterChange={setAdditionalFilters}
													applyFilters={applyFilters}
												/>
											)}
										</CatalogHiddenList>
									)
								)}
							</li>
						))}
					</CatalogItemList>
				</CatalogItemWrapper>
			)}
		</>
	)
}
