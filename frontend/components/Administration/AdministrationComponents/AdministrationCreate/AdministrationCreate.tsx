import { ICatalogSub } from '@/components/Catalog/Catalog-data/CatalogInterface/Catalog.interface'
import { IFields } from '@/components/Catalog/Catalog-data/CatalogInterface/Filter.interface'
import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { DropDown } from '@/components/DropDown/DropDown'
import { ProfileTitle } from '@/components/Profile/ProfileContent/ProfileContent.styled'
import {
	postListings as adminPostListings,
	getAllUsers,
} from '@/services/administration'
import { getSubCategories } from '@/services/getCategories'
import {
	getFieldsById,
	getInvestFieldsById,
	ICreateListing,
} from '@/services/getListings'
import {
	postDoc,
	postImage,
	postImages,
	postVideo,
} from '@/services/uploadFiles'
import { colors } from '@/utils'
import { AxiosError } from 'axios'
import { useCallback, useEffect, useState } from 'react'
import toast from 'react-hot-toast'

import { CreateModal } from '@/components/Profile/ProfileTabs/Listings/ListingComponents/CreateModal'
import {
	ListingButton,
	ListingField,
	ListingFields,
	ListingLabel,
	ListingMain,
	ListingTitle,
	ListingWrapper,
} from '@/components/Profile/ProfileTabs/Listings/Listings.styled'
import { Checkbox } from '@mui/material'
import FormControlLabel from '@mui/material/FormControlLabel'
import FormGroup from '@mui/material/FormGroup'
import Switch from '@mui/material/Switch'
import { useTranslation } from 'next-i18next'
import Select from 'react-select'
import { IUser } from '../AdministrationAcc/Admin.interface'

type InvestOptionValue = 'true' | 'false'

export const AdministrationCreate = () => {
	const {
		categories,
		setSubCategories,
		subCategories,
		activeCategory,
		setActiveCategory,
		setFields,
		fields,
	} = useCatalog()

	const [formData, setFormData] = useState({
		userId: '',
		title: '',
		description: '',
		price: '',
		invest: false,
		country: '',
		city: '',
		fullAddress: '',
		images: '',
		mainImage: '',
		videoUrl: '',
		fields: {} as Record<string, string>,
		status: 'ACTIVE',
	})

	const [selectedInvestType, setSelectedInvestType] =
		useState<InvestOptionValue | null>(null)
	const [selectedFile, setSelectedFile] = useState<File | null>(null)
	const [selectedVideo, setSelectedVideo] = useState<File | null>(null)
	const [selectedFiles, setSelectedFiles] = useState<File[]>([])
	const [selectedDynamicFiles, setSelectedDynamicFiles] = useState<
		Record<string, File | null>
	>({})
	const { t } = useTranslation('common')
	const [error, setError] = useState('')
	const [isSubmitting, setIsSubmitting] = useState(false)
	const [selectedSubCategory, setSelectedSubCategory] =
		useState<ICatalogSub | null>(null)
	const [isCreated, setIsCreated] = useState(false)
	const [listingId, setListingId] = useState<number>(0)
	const countryOptions = [
		{ value: 'Узбекистан', label: t('options.countries.uzbekistan') },
		{ value: 'Казахстан', label: t('options.countries.kazakhstan') },
		{
			value: 'Киргизская Республика',
			label: t('options.countries.kyrgyzRepublic'),
		},
		{ value: 'Таджикистан', label: t('options.countries.tajikistan') },
		{ value: 'Туркменистан', label: t('options.countries.turkmenistan') },
		{ value: 'Азербайджан', label: t('options.countries.azerbaijan') },
		{ value: 'Грузия', label: t('options.countries.georgia') },
		{ value: 'Армения', label: t('options.countries.armenia') },
		{ value: 'Украина', label: t('options.countries.ukraine') },
		{ value: 'Турция', label: t('options.countries.turkey') },
		{ value: 'Россия', label: t('options.countries.russia') },
		{ value: 'Республика Беларусь', label: t('options.countries.belarus') },
		{ value: 'Китай', label: t('options.countries.china') },
	]
	const listFieldOptions: Record<string, string[]> = {
		[t('options.businessDevelopmentStage.title')]: [
			t('options.businessDevelopmentStage.idea'),
			t('options.businessDevelopmentStage.mvp'),
			t('options.businessDevelopmentStage.growthStage'),
			t('options.businessDevelopmentStage.profitableBusiness'),
		],
		[t('options.formOfParticipation.title')]: [
			t('options.formOfParticipation.companyShare'),
			t('options.formOfParticipation.loan'),
			t('options.formOfParticipation.mentorshipPlusMoney'),
		],
		[t('options.transactionType.title')]: [
			t('options.transactionType.ventureInvestments'),
			t('options.transactionType.partialFullBuyout'),
			t('options.transactionType.jointVenture'),
			t('options.transactionType.credit'),
		],
	}
	const [users, setUsers] = useState<IUser[]>([])
	const [searchQuery, setSearchQuery] = useState<string>('')
	const [filteredUsers, setFilteredUsers] = useState<IUser[]>([])

	useEffect(() => {
		const fetchUsers = async () => {
			try {
				const response = await getAllUsers()
				setUsers(response)
			} catch (error) {
				console.error('Error fetching users:', error)
			}
		}

		fetchUsers()
	}, [])

	useEffect(() => {
		if (users && searchQuery.length >= 1) {
			const filtered = users.filter(user => {
				const fullName = `${user.name}`.toLowerCase()
				const query = searchQuery.toLowerCase()

				return fullName.includes(query) || user.id.toString().includes(query)
			})

			setFilteredUsers(filtered)
		} else {
			setFilteredUsers([])
		}
	}, [searchQuery, users])

	const handleFieldChange = (fieldName: string, value: string) => {
		setFormData(prev => ({
			...prev,
			fields: {
				...prev.fields,
				[fieldName]: value,
			},
		}))
	}

	const handleInvestChange = useCallback(
		(value: string) => {
			const investValue = value as InvestOptionValue
			setSelectedInvestType(investValue)
			setFormData(prev => ({
				...prev,
				invest: investValue === 'true',
				fields: {},
			}))
			setSelectedDynamicFiles({})
			setActiveCategory(null)
			setSubCategories([])
			setSelectedSubCategory(null)
			setFields(null)
			setError('')
		},
		[
			setActiveCategory,
			setSubCategories,
			setSelectedSubCategory,
			setFormData,
			setFields,
			setSelectedDynamicFiles,
		]
	)

	const fetchFieldsData = useCallback(
		async (
			currentFormDataFields: Record<string, string>
		): Promise<{
			fieldsData: IFields | null
			initialFormDataFields: Record<string, string>
		}> => {
			if (!activeCategory) {
				setFields(null)
				return { fieldsData: null, initialFormDataFields: {} }
			}

			try {
				let fieldsResponse: IFields | null = null

				if (selectedInvestType === 'true') {
					fieldsResponse = await getInvestFieldsById({ id: activeCategory.id })
				} else if (selectedInvestType === 'false') {
					fieldsResponse = await getFieldsById({ id: activeCategory.id })
				}

				let fieldsData: IFields | null = null
				if (fieldsResponse) {
					fieldsData = fieldsResponse
				}

				const initialFormDataFields: Record<string, string> = {}

				if (fieldsData?.fields) {
					fieldsData.fields.forEach(field => {
						if (field.type === 'Boolean' && field.required) {
							initialFormDataFields[field.name] =
								currentFormDataFields[field.name] !== undefined
									? currentFormDataFields[field.name]
									: 'false'
						} else if (
							field.type !== 'File' &&
							currentFormDataFields[field.name] !== undefined
						) {
							initialFormDataFields[field.name] =
								currentFormDataFields[field.name]
						}
					})
				}

				return { fieldsData, initialFormDataFields }
			} catch (error) {
				console.error('❌ Ошибка при получении данных полей:', error)
				setFields(null)
				return { fieldsData: null, initialFormDataFields: {} }
			}
		},
		[activeCategory, setFields, selectedInvestType]
	)

	useEffect(() => {
		setSelectedDynamicFiles({})

		const userIdNum = Number(formData.userId)
		if (activeCategory && !isNaN(userIdNum) && userIdNum > 0) {
			fetchFieldsData(formData.fields).then(
				({ fieldsData, initialFormDataFields }) => {
					setFields(fieldsData)
					setFormData(prev => ({
						...prev,
						fields: { ...initialFormDataFields },
					}))
				}
			)
		} else {
			setFields(null)
			setFormData(prev => ({
				...prev,
				fields: {},
			}))
		}
	}, [activeCategory, fetchFieldsData, formData.userId])

	const dropDownOptions =
		categories?.map(category => ({
			value: category.id.toString(),
			label: category.name,
		})) || []

	const dropDownOptionsSub =
		subCategories?.map(sub => ({
			value: sub.id.toString(),
			label: sub.name,
		})) || []

	const handleCategoryChange = useCallback(
		async (categoryId: string) => {
			try {
				const category = categories?.find(
					cat => cat.id.toString() === categoryId
				)
				if (!category) {
					setActiveCategory(null)
					setSubCategories([])
					setSelectedSubCategory(null)
					setFields(null)
					setFormData(prev => ({ ...prev, fields: {} }))
					setSelectedDynamicFiles({})
					setError('')
					return
				}

				setActiveCategory(category)
				setFormData(prev => ({ ...prev, fields: {} }))
				setSelectedDynamicFiles({})

				const subCategoriesData = await getSubCategories({ id: category.id })
				setSubCategories(subCategoriesData)
				setSelectedSubCategory(null)
				setError('')
			} catch (error) {
				console.error('❌ Ошибка при получении подкатегорий:', error)
				setSubCategories([])
				setSelectedSubCategory(null)
				setFields(null)
				setFormData(prev => ({ ...prev, fields: {} }))
				setSelectedDynamicFiles({})
				setError('Не удалось загрузить подкатегории.')
			}
		},
		[
			categories,
			setActiveCategory,
			setSubCategories,
			setSelectedSubCategory,
			setFormData,
			setFields,
			setSelectedDynamicFiles,
		]
	)

	const handleSubCategoryChange = (subCategoryId: string) => {
		const subCategory = subCategories?.find(
			sub => sub.id.toString() === subCategoryId
		)
		setSelectedSubCategory(subCategory || null)
		setFormData(prev => ({ ...prev, fields: {} }))
		setSelectedDynamicFiles({})
		setError('')
	}

	const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		if (event.target.files?.[0]) {
			setSelectedFile(event.target.files[0])
			setError('')
		}
	}

	const handleMultipleFileChange = (
		event: React.ChangeEvent<HTMLInputElement>
	) => {
		const files = event.target.files
		if (files) {
			setSelectedFiles(Array.from(files))
			setError('')
		}
	}

	const handleVideoChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		if (event.target.files?.[0]) {
			setSelectedVideo(event.target.files[0])
			setError('')
		}
	}

	const handleDynamicFileChange = (
		fieldName: string,
		event: React.ChangeEvent<HTMLInputElement>
	) => {
		const file = event.target.files?.[0] || null
		setSelectedDynamicFiles(prev => ({
			...prev,
			[fieldName]: file,
		}))
		setError('')
	}
	const handleMultiSelectChange = (
		fieldName: string,
		option: string,
		checked: boolean
	) => {
		const currentRaw = formData.fields[fieldName]
		const currentValues = Array.isArray(currentRaw) ? currentRaw : []

		const updatedValues = checked
			? [...new Set([...currentValues, option])]
			: currentValues.filter((val: string) => val !== option)

		// eslint-disable-next-line @typescript-eslint/no-explicit-any
		setFormData((prev: any) => ({
			...prev,
			fields: {
				...prev.fields,
				[fieldName]: updatedValues,
			},
		}))
	}
	const handlePostListings = async () => {
		setError('')
		setIsSubmitting(true)

		const userIdNum = Number(formData.userId)
		if (!formData.userId || isNaN(userIdNum) || userIdNum <= 0) {
			setError('Пожалуйста, введите корректный ID пользователя.')
			toast.error('Пожалуйста, введите корректный ID пользователя.')
			setIsSubmitting(false)
			return
		}

		if (selectedInvestType === null) {
			setError('Пожалуйста, выберите тип объявления.')
			toast.error('Пожалуйста, выберите тип объявления.')
			setIsSubmitting(false)
			return
		}
		if (!activeCategory) {
			setError('Пожалуйста, выберите категорию.')
			toast.error('Пожалуйста, выберите категорию.')
			setIsSubmitting(false)
			return
		}
		if (
			!formData.title ||
			!formData.description ||
			!formData.price ||
			!formData.country ||
			!formData.city ||
			!formData.fullAddress
		) {
			toast.error(
				'Пожалуйста, заполните все обязательные поля основной информации.'
			)
			setIsSubmitting(false)
			return
		}
		if (isNaN(Number(formData.price))) {
			setError('Цена должна быть числом.')
			toast.error('Цена должна быть числом.')
			setIsSubmitting(false)
			return
		}
		if (!selectedFile) {
			toast.error('Пожалуйста, загрузите главную фотографию.')
			setIsSubmitting(false)
			return
		}

		if (fields?.fields) {
			for (const field of fields.fields) {
				const value = formData.fields[field.name]
				const file = selectedDynamicFiles[field.name]

				if (field.required) {
					if (field.type === 'Boolean') {
						if (value === undefined) {
							toast.error(
								`Пожалуйста, заполните обязательное булевое поле: "${field.name}"`
							)
							setIsSubmitting(false)
							return
						}
					} else if (field.type === 'File') {
						if (!file) {
							toast.error(
								`Пожалуйста, загрузите обязательный файл: "${field.name}"`
							)
							setIsSubmitting(false)
							return
						}
					} else {
						if (!value || (typeof value === 'string' && value.trim() === '')) {
							toast.error(
								`Пожалуйста, заполните обязательное поле: "${field.name}"`
							)
							setIsSubmitting(false)
							return
						}
					}
				}

				if (
					(field.type === 'Double' || field.type === 'Integer') &&
					value &&
					isNaN(Number(value))
				) {
					toast.error(`Поле "${field.name}" должно быть числом.`)
					setIsSubmitting(false)
					return
				}
			}
		}

		try {
			const uploadPromises: Promise<{ fieldName: string; url: string }>[] = []

			if (selectedFile) {
				uploadPromises.push(
					postImage(selectedFile).then(url => ({ fieldName: 'mainImage', url }))
				)
			}

			if (selectedVideo) {
				uploadPromises.push(
					postVideo(selectedVideo).then(url => ({ fieldName: 'videoUrl', url }))
				)
			}

			if (fields?.fields) {
				fields.fields.forEach(fieldDefinition => {
					if (fieldDefinition.type === 'File') {
						const fieldName = fieldDefinition.name
						const file = selectedDynamicFiles[fieldName]
						if (file) {
							uploadPromises.push(
								postDoc(file).then(url => ({ fieldName, url }))
							)
						}
					}
				})
			}

			const uploadResults = await Promise.all(uploadPromises)

			const uploadedUrls: Record<string, string> = {}
			const galleryUrls: string[] = []
			if (selectedFiles.length > 0) {
				const savedUrls = await postImages(selectedFiles)
				galleryUrls.push(...savedUrls)
			}
			uploadResults.forEach(result => {
				uploadedUrls[result.fieldName] = result.url
			})

			const price = Number(formData.price)

			const processedFields: Record<string, string | number | boolean> = {}

			if (fields?.fields) {
				fields.fields.forEach(fieldDefinition => {
					const fieldName = fieldDefinition.name
					const fieldType = fieldDefinition.type
					let finalValue: string | number | boolean | undefined

					if (fieldType === 'File') {
						finalValue = uploadedUrls[fieldName] || ''
						if (finalValue) {
							processedFields[fieldName] = finalValue
						}
					} else if (fieldType === 'Boolean') {
						if (formData.fields[fieldName] !== undefined) {
							finalValue = formData.fields[fieldName] === 'true'
							processedFields[fieldName] = finalValue
						} else if (fieldDefinition.required) {
							processedFields[fieldName] = false
						}
					} else if (fieldType === 'Double' || fieldType === 'Integer') {
						const numValue = Number(formData.fields[fieldName])
						if (!isNaN(numValue)) {
							finalValue =
								fieldType === 'Double'
									? Number.isInteger(numValue)
										? numValue + 0.0001
										: numValue
									: numValue
							processedFields[fieldName] = finalValue
						} else if (fieldDefinition.required) {
							processedFields[fieldName] = fieldType === 'Double' ? 0.0 : 0
						}
					} else {
						finalValue = formData.fields[fieldName]
						if (finalValue !== undefined && finalValue !== '') {
							processedFields[fieldName] = finalValue
						} else if (fieldDefinition.required) {
							processedFields[fieldName] = ''
						}
					}
				})
			}
			const listingData: ICreateListing = {
				title: formData.title,
				categoryId: activeCategory!.id,
				subCategoryId: selectedSubCategory
					? Number(selectedSubCategory.id)
					: activeCategory!.id,
				description: formData.description,
				price: price,
				mainImage: uploadedUrls['mainImage'] || '',
				images: galleryUrls,
				videoUrl: uploadedUrls['videoUrl'] || '',
				invest: formData.invest,
				country: formData.country,
				city: formData.city,
				fullAddress: formData.fullAddress,
				premiumStartDate: null,
				premiumEndDate: null,
				fields: processedFields,
				status: 'ACTIVE',
			}
			const listing = await adminPostListings(listingData, userIdNum)
			setListingId(listing.id || null)
			setIsCreated(true)

			setFormData({
				userId: '',
				title: '',
				description: '',
				price: '',
				country: '',
				invest: false,
				city: '',
				fullAddress: '',
				mainImage: '',
				images: '',
				videoUrl: '',
				fields: {},
				status: 'ACTIVE',
			})
			setSelectedFile(null)
			setSelectedVideo(null)
			setSelectedDynamicFiles({})
			setSelectedInvestType(null)
			setActiveCategory(null)
			setSubCategories([])
			setSelectedSubCategory(null)
			setFields(null)
		} catch (error) {
			const err = error as AxiosError
			console.error('❌ Ошибка при создании листинга:', err.message || err)
			toast.error(
				'Ошибка при создании листинга. Пожалуйста, проверьте данные и попробуйте еще раз.'
			)
		} finally {
			setIsSubmitting(false)
		}
	}

	return (
		<>
			     {' '}
			<ListingWrapper>
				       {' '}
				<div>
					     {' '}
					<div style={{ position: 'relative', width: '300px' }}>
						<input
							type='text'
							value={searchQuery}
							onChange={e => {
								setSearchQuery(e.target.value)
								setFormData({ ...formData, userId: e.target.value })
							}}
							placeholder='Введите имя пользователя'
							style={{
								width: '100%',
								padding: '10px',
								borderRadius: '5px',
								border: '1px solid #ccc',
								outline: 'none',
							}}
						/>

						{filteredUsers.length > 0 && (
							<ul
								style={{
									position: 'absolute',
									top: '100%',
									left: 0,
									right: 0,
									background: '#fff',
									border: '1px solid #ccc',
									maxHeight: '200px',
									overflowY: 'auto',
									zIndex: 1000,
									borderRadius: '4px',
									margin: 0,
									padding: 0,
									listStyle: 'none',
								}}
							>
								{filteredUsers.map(user => (
									<li
										key={user.id}
										onClick={() => {
											setFormData({ ...formData, userId: String(user.id) })
											setSearchQuery(`${user.id}`)
											setFilteredUsers([])
										}}
										style={{
											padding: '10px',
											cursor: 'pointer',
											borderBottom: '1px solid #eee',
										}}
									>
										{user.name} || ID - {user.id}
									</li>
								))}
							</ul>
						)}
					</div>
					     
				</div>
				     
				<ListingTitle>
					         
					<ProfileTitle bgc='#CABDFF'>{t('listingCreate.title1')}</ProfileTitle>
					     
				</ListingTitle>
				<div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
					       
					<button
						onClick={() => handleInvestChange('true')}
						style={{
							padding: '10px 15px',
							border: `1px solid ${colors.btnMainColor}`,
							borderRadius: '5px',
							backgroundColor:
								selectedInvestType === 'true'
									? colors.btnMainColor
									: colors.mainWhiteTextColor,
							color:
								selectedInvestType === 'true'
									? colors.mainWhiteTextColor
									: colors.mainTextColor,
							cursor: 'pointer',
							fontSize: '1rem',
							transition:
								'background-color 0.3s, color 0.3s, border-color 0.3s',
							opacity: isSubmitting ? 0.6 : 1,
							pointerEvents: isSubmitting ? 'none' : 'auto',
						}}
						disabled={isSubmitting}
					>
						{t('listingCreate.investType')}
					</button>
					         
					<button
						onClick={() => handleInvestChange('false')}
						style={{
							padding: '10px 15px',
							border: `1px solid ${colors.btnMainColor}`,
							borderRadius: '5px',
							backgroundColor:
								selectedInvestType === 'false'
									? colors.btnMainColor
									: colors.mainWhiteTextColor,
							color:
								selectedInvestType === 'false'
									? colors.mainWhiteTextColor
									: colors.mainTextColor,
							cursor: 'pointer',
							fontSize: '1rem',
							transition:
								'background-color 0.3s, color 0.3s, border-color 0.3s',
							opacity: isSubmitting ? 0.6 : 1,
							pointerEvents: isSubmitting ? 'none' : 'auto',
						}}
						disabled={isSubmitting}
					>
						{t('listingCreate.businessSale')}
					</button>
					     
				</div>
				           
				{selectedInvestType !== null && (
					<>
						<ListingTitle>
							<ProfileTitle bgc='#CABDFF'>
								{t('listingCreate.title2')}
							</ProfileTitle>
						</ListingTitle>
						<div
							style={{
								display: 'flex',
								alignItems: 'center',
								justifyContent: 'start',
								gap: '10px',
							}}
						>
							<DropDown
								options={dropDownOptions}
								placeholder={t('listingCreate.placeholderCat')}
								isRounded={true}
								width='240px'
								onChange={handleCategoryChange}
							/>
							{subCategories?.length > 0 ? (
								<DropDown
									options={dropDownOptionsSub}
									placeholder={t('listingCreate.placeholderSubCat')}
									isRounded={true}
									width='240px'
									onChange={handleSubCategoryChange}
								/>
							) : activeCategory && selectedInvestType !== null ? (
								<p>{t('listingCreate.noSubCatFound')}</p>
							) : selectedInvestType !== null ? (
								<p>{t('listingCreate.subCatPending')}</p>
							) : null}
						</div>
					</>
				)}
				{selectedInvestType !== null && activeCategory && (
					<>
						<ListingTitle>
							<ProfileTitle bgc={colors.btnSecondColor}>
								{t('listingCreate.title3')}
							</ProfileTitle>{' '}
							         
						</ListingTitle>
						<ListingMain>
							   {error && <p style={{ color: 'red' }}>{error}</p>} 
							{[
								{
									name: 'title',
									label: t('listingCreate.fieldTitle'),
									type: 'text',
								},
								{
									name: 'description',
									label: t('listingCreate.fieldDescription'),
									type: 'textarea',
								},
								{
									name: 'price',
									label: t('listingCreate.fieldPriceBudget'),
									type: 'number',
								},
								{
									name: 'country',
									label: t('listingCreate.fieldCountry'),
									type: 'text',
								},
								{
									name: 'city',
									label: t('listingCreate.fieldCity'),
									type: 'text',
								},
								{
									name: 'fullAddress',
									label: t('listingCreate.fieldFullAddress'),
									type: 'text',
								},
							].map(field => (
								<ListingField key={field.name}>
									<ListingLabel>{field.label}</ListingLabel>               
									{field.type === 'textarea' ? (
										<textarea
											name={field.name}
											className='textarea'
											rows={4}
											cols={35}
											value={
												formData[
													field.name as keyof Omit<
														typeof formData,
														'userId' | 'invest' | 'fields' | 'status'
													>
												] as string
											}
											onChange={e =>
												setFormData({
													...formData,
													[field.name]: e.target.value,
												})
											}
											placeholder={field.label}
											disabled={isSubmitting}
											required
										/>
									) : field.name === 'country' ? (
										<Select
											options={countryOptions}
											value={countryOptions.find(
												option => option.label === formData.country
											)}
											onChange={selectedOption =>
												setFormData({
													...formData,
													country: selectedOption?.label || '',
												})
											}
											placeholder={t('placeholders.chooseCountry')}
											isDisabled={isSubmitting}
										/>
									) : (
										<input
											type={field.type}
											name={field.name}
											className={field.type}
											value={
												formData[
													field.name as keyof Omit<
														typeof formData,
														'userId' | 'invest' | 'fields' | 'status'
													>
												]?.toString() || ''
											}
											onChange={e =>
												setFormData({
													...formData,
													[field.name]: e.target.value,
												})
											}
											placeholder={field.label}
											required
											disabled={isSubmitting}
										/>
									)}
									           
								</ListingField>
							))}
							                         
							<ListingField>
								<ListingLabel>{t('listingCreate.mainPhoto')}</ListingLabel>    
								       
								<input
									type='file'
									accept='image/*'
									onChange={handleFileChange}
									disabled={isSubmitting}
									required
								/>
								               {' '}
								{selectedFile && <span>{selectedFile.name}</span>}
							</ListingField>
							<ListingField>
								<ListingLabel>{t('listingCreate.galleryPhotos')}</ListingLabel>
								<input
									type='file'
									accept='image/*'
									multiple
									onChange={handleMultipleFileChange}
								/>
								{selectedFiles.length > 0 && (
									<ul>
										{selectedFiles.map((file, index) => (
											<li key={index}>{file.name}</li>
										))}
									</ul>
								)}
							</ListingField>
							             {' '}
							<ListingField>
								               {' '}
								<ListingLabel>{t('listingCreate.video')}</ListingLabel>         
								     {' '}
								<input
									type='file'
									accept='video/*'
									onChange={handleVideoChange}
									disabled={isSubmitting}
								/>
								               {' '}
								{selectedVideo && <span>{selectedVideo.name}</span>}           
								 {' '}
							</ListingField>
							           {' '}
						</ListingMain>
						         {' '}
					</>
				)}
				               {' '}
				{selectedInvestType !== null && activeCategory && (
					<ListingTitle>
						           {' '}
						<ProfileTitle bgc={colors.btnMainColor}>
							{t('listingCreate.additionalFieldsTitle')}
						</ProfileTitle>
						         {' '}
					</ListingTitle>
				)}
				               {' '}
				{selectedInvestType !== null &&
					activeCategory &&
					fields?.fields &&
					fields.fields.length > 0 && (
						<ListingFields>
							{fields.fields.map(field => {
								let inputType = 'text'
								let stepValue: string | undefined = undefined
								let accept: string | undefined = undefined

								if (field.type === 'Double') {
									inputType = 'number'
									stepValue = '0.01'
								} else if (field.type === 'Boolean') {
									inputType = 'checkbox'
								} else if (field.type === 'Integer') {
									inputType = 'number'
								} else if (field.type === 'File') {
									inputType = 'file'
									accept = '*'
								} else if (field.type === 'String') {
									inputType = 'text'
								}

								return (
									<ListingField key={field.name}>
										<ListingLabel>
											{field.name}
											{field.name === 'Роялти' && ' %'}
											{field?.required && (
												<span style={{ color: 'red' }}> *</span>
											)}
										</ListingLabel>

										{field.type === 'List' && listFieldOptions[field.name] ? (
											<>
												<FormGroup>
													{listFieldOptions[field.name].map(option => (
														<FormControlLabel
															key={option}
															control={
																<Checkbox
																	checked={
																		Array.isArray(
																			formData.fields[field.name]
																		) &&
																		formData.fields[field.name].includes(option)
																	}
																	onChange={e =>
																		handleMultiSelectChange(
																			field.name,
																			option,
																			e.target.checked
																		)
																	}
																	disabled={isSubmitting}
																/>
															}
															label={option}
														/>
													))}
												</FormGroup>
											</>
										) : inputType === 'textarea' ? (
											<textarea
												name={field.name}
												className='textarea'
												rows={4}
												cols={15}
												value={formData.fields[field.name] || ''}
												onChange={e =>
													handleFieldChange(field.name, e.target.value)
												}
												placeholder={`${field.name}${
													field?.required ? '*' : ''
												}`}
												disabled={isSubmitting}
												required={field.required}
											/>
										) : inputType === 'checkbox' ? (
											<FormGroup>
												<FormControlLabel
													control={
														<Switch
															style={{
																display: 'flex',
																alignItems: 'flex-start',
																maxHeight: '20px',
															}}
															checked={formData.fields[field.name] === 'true'}
															onChange={e =>
																handleFieldChange(
																	field.name,
																	e.target.checked.toString()
																)
															}
															disabled={isSubmitting}
														/>
													}
													label={`${field.name}${field?.required ? '*' : ''}`}
													sx={{
														color:
															formData.fields[field.name] === 'true'
																? colors.btnMainColor
																: colors.greyTextColor,
													}}
												/>
											</FormGroup>
										) : inputType === 'file' ? (
											<>
												<input
													type='file'
													accept={accept}
													onChange={e => handleDynamicFileChange(field.name, e)}
													disabled={isSubmitting}
													required={field.required}
												/>
												{selectedDynamicFiles[field.name] && (
													<span>{selectedDynamicFiles[field.name]?.name}</span>
												)}
											</>
										) : (
											<input
												type={inputType}
												step={stepValue}
												placeholder={`${field.name}${
													field?.required ? '*' : ''
												}`}
												value={formData.fields[field.name]?.toString() || ''}
												onChange={e =>
													handleFieldChange(field.name, e.target.value)
												}
												disabled={isSubmitting}
												required={field.required}
											/>
										)}
									</ListingField>
								)
							})}
						</ListingFields>
					)}
				       
				{selectedInvestType !== null &&
					activeCategory &&
					(!fields?.fields || fields.fields.length === 0) && (
						<p>{t('listingCreate.noAdditionalFields')}</p>
					)}
				       
				{selectedInvestType !== null && activeCategory && (
					<ListingButton onClick={handlePostListings} disabled={isSubmitting}>
						{isSubmitting ? (
							<h1>{t('listingCreate.creatingLoading')}</h1>
						) : (
							t('listingCreate.createListingButton')
						)}
					</ListingButton>
				)}
				     
			</ListingWrapper>
			{isCreated && (
				<CreateModal
					listingId={listingId}
					closeModal={() => setIsCreated(false)}
				/>
			)}
			   {' '}
		</>
	)
}
