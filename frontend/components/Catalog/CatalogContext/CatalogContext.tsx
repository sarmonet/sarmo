import { IContent } from '@/components/Blog/ContentInterface/Content.interface'
import { IChat } from '@/components/Chat/Chat.interface'
import { IListingItem } from '@/components/Listing/Listing.interface'
import { IUser } from '@/components/Profile/ProfileInterface/Profile.interface'
import { getCategories } from '@/services/getCategories'
import {
	FilterParams,
	Filters,
	getFavoriteAll,
	getListings,
} from '@/services/getListings'
import { getUser } from '@/services/getUsers'
import { AxiosError } from 'axios'
import {
	createContext,
	useCallback,
	useContext,
	useEffect,
	useState,
} from 'react'
import {
	ICatalog,
	ICatalogSub,
} from '../Catalog-data/CatalogInterface/Catalog.interface'
import { IFields } from '../Catalog-data/CatalogInterface/Filter.interface'
import { IMongo } from '../Catalog-data/CatalogInterface/Mongo.interface'

interface CatalogContextProps {
	listings: IListingItem[] | null
	categories: ICatalog[]
	subCategories: ICatalogSub[]
	fields: IFields | null
	favorite: IListingItem[] | null
	count: number
	currentPage: number
	totalPages: number
	filteredListings: {
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		premiumListings: any | null
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		paginatedListings: any | null
	} | null
	//TODO: ВЕРНУТЬ IListingItem В ФИЛЬТР
	activeCategory: ICatalog | null
	selectedSubCategory: ICatalogSub | null
	navigating: boolean
	verificationId: string
	user: IUser | null
	chatUser: IChat | null
	chatId: number | null
	loadingListings: boolean
	errorListings: Error | null
	filters: Filters | null
	mongoFilter: IMongo
	blog: IContent[]
	filteredParams: FilterParams | null
	activeTab: number
	setActiveTab: (activeTab: number) => void
	setFilteredParams: (params: FilterParams | null) => void
	setListings: (listings: IListingItem[]) => void
	setCategories: (categories: ICatalog[]) => void
	setFields: (fields: IFields | null) => void
	setCount: (count: number) => void
	setCurrentPage: (currentPage: number) => void
	setTotalPages: (totalPages: number) => void
	setFavorite: (favorite: IListingItem[] | null) => void
	setSubCategories: (subCategories: ICatalogSub[]) => void
	setVerificationId: (verificationId: string) => void
	setFilteredListings: (
		filteredListings: {
			premiumListings: IListingItem[] | null
			paginatedListings: IListingItem[] | null
		} | null
	) => void
	setActiveCategory: (activeCategory: ICatalog | null) => void
	setSelectedSubCategory: (selectedSubCategory: ICatalogSub | null) => void
	isFilterReset: boolean
	setIsNavigating: (isNavigating: boolean) => void
	setIsFilterReset: (isFilterReset: boolean) => void
	setUser: (user: IUser | null) => void
	setChatUser: (chatUser: IChat | null) => void
	setChatId: (chatUserId: number | null) => void
	setFilters: (filters: Filters) => void
	setMongo: (mongoFilter: IMongo) => void
	updateFavorite: () => Promise<void>
	setBlog: (blog: IContent[]) => void
}

const CatalogContext = createContext<CatalogContextProps | undefined>(undefined)

export const CatalogProvider = ({
	children,
}: {
	children: React.ReactNode
}) => {
	const [listings, setListings] = useState<IListingItem[]>([])
	const [categories, setCategories] = useState<ICatalog[]>([])
	const [subCategories, setSubCategories] = useState<ICatalogSub[]>([])
	const [fields, setFields] = useState<IFields | null>(null)
	const [favorite, setFavorite] = useState<IListingItem[] | null>([])
	const [count, setCount] = useState<number>(0)
	const [currentPage, setCurrentPage] = useState<number>(1)
	const [totalPages, setTotalPages] = useState<number>(0)
	const [verificationId, setVerificationId] = useState<string>('')
	const [filteredListings, setFilteredListings] = useState<{
		premiumListings: IListingItem[] | null
		paginatedListings: IListingItem[] | null
	} | null>(null)
	const [activeCategory, setActiveCategory] = useState<ICatalog | null>(null)
	const [selectedSubCategory, setSelectedSubCategory] =
		useState<ICatalogSub | null>(null)
	const [filteredParams, setFilteredParams] = useState<FilterParams | null>(
		null
	)
	const [navigating, setIsNavigating] = useState(false)
	const [isFilterReset, setIsFilterReset] = useState(false)
	const [user, setUser] = useState<IUser | null>(null)
	const [chatUser, setChatUser] = useState<IChat | null>(null)
	const [chatId, setChatId] = useState<number | null>(null)
	const [loadingListings, setLoadingListings] = useState(false)
	const [errorListings, setErrorListings] = useState<Error | null>(null)
	const [filters, setFilters] = useState<Filters | null>(null)
	const [mongoFilter, setMongo] = useState<IMongo>({ fields: {} })
	const [blog, setBlog] = useState<IContent[]>([])
	const [activeTab, setActiveTab] = useState(0)

	const fetchListings = useCallback(async () => {
		setLoadingListings(true)
		setErrorListings(null)
		try {
			const data = await getListings(currentPage)
			setListings(data.content)
			setTotalPages(data.totalPages)
			setCount(data.totalElements)
		} catch (error) {
			setErrorListings(error as Error)
		} finally {
			setLoadingListings(false)
		}
	}, [currentPage])

	useEffect(() => {
		fetchListings()
	}, [fetchListings])

	const updateFavorite = async () => {
		try {
			const data = await getFavoriteAll()
			if (!data || data.length === 0) {
				setFavorite(null)
				return
			}

			setFavorite(data)
		} catch (error) {
			const err = error as AxiosError

			if (err.response?.status === 500) {
				console.error('🚨 Ошибка сервера (500): Избранное недоступно')
			} else {
				console.error('❌ Ошибка загрузки избранного:', err.message)
			}

			setFavorite(null)
		}
	}

	useEffect(() => {
		updateFavorite()
	}, [])

	useEffect(() => {
		const fetchUser = async () => {
			try {
				if (typeof window === 'undefined') return
				const accessToken = localStorage.getItem('accessToken')
				if (!accessToken) {
					setUser(null)
					return
				}
				const userData = await getUser()
				if (userData) {
					setUser(userData)
				} else if (userData.length === 0) {
					setUser(null)
					return
				}
			} catch (error) {
				const err = error as AxiosError

				if (err.response?.status === 403) {
					console.warn('🚨 пользователь не вошёл в аккаунт')
					setUser(null)
				} else {
					console.error('❌ Ошибка юзера:', err.message)
					setUser(null)
				}
			}
		}

		fetchUser()
	}, [setUser])

	useEffect(() => {
		const fetchCategories = async () => {
			try {
				const data = await getCategories()
				setCategories(data)
			} catch (error) {
				console.error('Ошибка загрузки категорий:', error)
			}
		}

		fetchCategories()
	}, [setCategories])

	return (
		<CatalogContext.Provider
			value={{
				listings,
				categories,
				subCategories,
				favorite,
				fields,
				count,
				currentPage,
				totalPages,
				verificationId,
				filteredListings,
				activeCategory,
				navigating,
				user,
				chatUser,
				chatId,
				selectedSubCategory,
				filters,
				mongoFilter,
				blog,
				filteredParams,
				activeTab,
				setActiveTab,
				setFilteredParams,
				setListings,
				setCategories,
				setSubCategories,
				setFields,
				setFavorite,
				setCount,
				setCurrentPage,
				setTotalPages,
				setVerificationId,
				setFilteredListings,
				setActiveCategory,
				setSelectedSubCategory,
				setIsNavigating,
				isFilterReset,
				setUser,
				setChatUser,
				setChatId,
				setIsFilterReset,
				loadingListings,
				errorListings,
				setFilters,
				setMongo,
				updateFavorite,
				setBlog,
			}}
		>
			{children}
		</CatalogContext.Provider>
	)
}

export const useCatalog = () => {
	const context = useContext(CatalogContext)
	if (!context) {
		throw new Error('useCatalog must be used within a CatalogProvider')
	}
	return context
}
