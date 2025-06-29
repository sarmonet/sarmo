'use client'

import {
	IContent,
	IContentItem,
} from '@/components/Blog/ContentInterface/Content.interface'
import { updateNew } from '@/services/getContent'
import { postImage } from '@/services/uploadFiles'
import { useTranslation } from 'next-i18next'
import dynamic from 'next/dynamic'
import Image from "next/legacy/image"
import { useEffect, useMemo, useState } from 'react'
import toast from 'react-hot-toast'
const ReactQuill = dynamic(() => import('react-quill'), { ssr: false })

interface Props {
	blogContent: IContent
	onEditComplete: () => void
}

export const EditNew = ({ blogContent, onEditComplete }: Props) => {
	const { t } = useTranslation('common')

	const [formData, setFormData] = useState<IContent>({
		id: blogContent.id,
		mainImage: blogContent.mainImage || '',
		title: blogContent.title || '',
		description: blogContent.description || '',
		content: blogContent.content || [],
	})
	const [selectedFile, setSelectedFile] = useState<File | null>(null)
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState<string | null>(null)

	const quillModules = useMemo(
		() => ({
			toolbar: [
				[{ header: [1, 2, 3, false] }],
				[{ font: [] }],
				['bold', 'italic', 'underline', 'strike'],
				[{ list: 'ordered' }, { list: 'bullet' }],
				['clean'],
			],
		}),
		[]
	)

	const quillFormats = [
		'header',
		'font',
		'bold',
		'italic',
		'underline',
		'strike',
		'list',
		'bullet',
	]

	useEffect(() => {
		setFormData({
			id: blogContent.id,
			mainImage: blogContent.mainImage || '',
			title: blogContent.title || '',
			description: blogContent.description || '',
			content: blogContent.content || [],
		})
		setSelectedFile(null)
	}, [blogContent])

	const handleChange = (
		e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
	) => {
		const { name, value } = e.target
		setFormData(prev => ({
			...prev,
			[name]: value,
		}))
	}

	const handleContentItemChange = (
		index: number,
		field: keyof IContentItem,
		value: string
	) => {
		setFormData(prev => {
			const newContent = [...prev.content]
			if (newContent[index]) {
				newContent[index] = {
					...newContent[index],
					[field]: value,
				}
			}
			return {
				...prev,
				content: newContent,
			}
		})
	}

	const handleAddContentItem = (type: 'text' | 'image') => {
		setFormData(prev => ({
			...prev,
			content: [...prev.content, { type, title: '', value: '' }],
		}))
	}

	const handleRemoveContentItem = (index: number) => {
		setFormData(prev => ({
			...prev,
			content: prev.content.filter((_, i) => i !== index),
		}))
	}

	const handleMainImageUpload = async (
		event: React.ChangeEvent<HTMLInputElement>
	) => {
		if (event.target.files?.[0]) {
			const file = event.target.files[0]
			setSelectedFile(file)
			try {
				const imageData = await postImage(file)
				setFormData(prev => ({ ...prev, mainImage: imageData }))
				setError(null)
			} catch (err) {
				console.error('Ошибка при загрузке главного изображения:', err)
				setError('Ошибка при загрузке главного изображения.')
			}
		}
	}

	const handleContentImageUpload = async (
		index: number,
		event: React.ChangeEvent<HTMLInputElement>
	) => {
		if (event.target.files?.[0]) {
			const file = event.target.files[0]
			try {
				const imageData = await postImage(file)
				handleContentItemChange(index, 'value', imageData)
				setError(null)
			} catch (err) {
				console.error('Ошибка при загрузке изображения для блока:', err)
				setError('Ошибка при загрузке изображения для блока.')
			}
		}
	}

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault()
		setLoading(true)
		setError(null)

		if (!formData.mainImage) {
			setError('Пожалуйста, загрузите главное изображение.')
			setLoading(false)
			return
		}

		if (typeof formData.id === 'undefined' || formData.id === null) {
			setError('ID блога не определен для обновления.')
			setLoading(false)
			return
		}

		try {
			await updateNew(Number(formData.id), formData)
			toast.success('Новость успешно обновлена!')
			onEditComplete()
		} catch (err) {
			console.error('Ошибка при сохранении блога:', err)
			setError('Не удалось сохранить изменения. Попробуйте еще раз.')
		} finally {
			setLoading(false)
		}
	}

	return (
		<div className='flex flex-col gap-8 p-6 bg-white shadow-md rounded-lg'>
			<h1 className='text-3xl font-semibold text-gray-800'>
				Редактировать блог
			</h1>

			<form onSubmit={handleSubmit} className='flex flex-col gap-6'>
				<div>
					<label
						htmlFor='title'
						className='block text-lg font-medium text-gray-700 mb-1'
					>
						Заголовок
					</label>
					<input
						id='title'
						name='title'
						type='text'
						value={formData.title}
						onChange={handleChange}
						required
						className='w-full p-2 border border-gray-300 rounded-md'
					/>
				</div>
				<div>
					<label
						htmlFor='description'
						className='block text-lg font-medium text-gray-700 mb-1'
					>
						Описание
					</label>
					<textarea
						id='description'
						name='description'
						value={formData.description}
						onChange={handleChange}
						required
						rows={3}
						className='w-full p-2 border border-gray-300 rounded-md'
					/>
				</div>
				<div>
					<label
						htmlFor='mainImage'
						className='block text-lg font-medium text-gray-700 mb-1'
					>
						Главное изображение (URL)
					</label>
					<input
						id='mainImage'
						name='mainImage'
						type='text'
						value={formData.mainImage}
						onChange={handleChange}
						className='w-full p-2 border border-gray-300 rounded-md mb-2'
						placeholder='Введите URL изображения или загрузите файл ниже'
					/>
					<input
						type='file'
						accept='image/*'
						onChange={handleMainImageUpload}
					/>
					{selectedFile && (
						<span className='text-sm text-gray-600 ml-2'>
							{selectedFile.name}
						</span>
					)}
					{formData.mainImage && (
						<div className='mt-2'>
							<Image
								width={200}
								height={200}
								src={formData.mainImage}
								alt='Главное изображение'
								className='max-w-[200px] h-auto rounded-md'
							/>
						</div>
					)}
				</div>

				<h2 className='text-2xl font-semibold text-gray-800 mt-4'>
					Контент блога
				</h2>
				{formData.content.length === 0 && (
					<p className='text-gray-500'>
						Контент отсутствует. Добавьте первый блок.
					</p>
				)}
				{Array.isArray(formData.content) &&
					formData.content.map((item, index) => (
						<div
							key={index}
							className='p-4 border border-gray-200 rounded-lg bg-gray-50 relative'
						>
							<button
								type='button'
								onClick={() => handleRemoveContentItem(index)}
								className='absolute top-2 right-2 text-red-500 hover:text-red-700'
							>
								Удалить блок
							</button>
							<h3 className='text-xl font-bold text-gray-800 mb-2'>
								Блок контента {index + 1} (
								{item.type === 'text' ? 'Текст' : 'Изображение'})
							</h3>

							<div className='mb-3'>
								<label
									htmlFor={`content-type-${index}`}
									className='block text-sm font-medium text-gray-600 mb-1'
								>
									Тип блока:
								</label>
								<select
									id={`content-type-${index}`}
									value={item.type}
									onChange={e =>
										handleContentItemChange(
											index,
											'type',
											e.target.value as 'text' | 'image'
										)
									}
									className='w-full p-2 border border-gray-300 rounded-md'
								>
									<option value='text'>Текстовый блок</option>
									<option value='image'>Блок изображения</option>
								</select>
							</div>

							<div>
								<label
									htmlFor={`content-title-${index}`}
									className='block text-sm font-medium text-gray-600 mb-1'
								>
									Заголовок блока
								</label>
								<input
									id={`content-title-${index}`}
									type='text'
									value={item.title}
									onChange={e =>
										handleContentItemChange(index, 'title', e.target.value)
									}
									className='w-full p-2 border border-gray-300 rounded-md'
								/>
							</div>
							<div className='mt-3'>
								<label
									htmlFor={`content-value-${index}`}
									className='block text-sm font-medium text-gray-600 mb-1'
								>
									Содержимое блока
								</label>
								{item.type === 'text' ? (
									<ReactQuill
										key={`editor-${index}`}
										value={item.value}
										theme='snow'
										onChange={html =>
											handleContentItemChange(index, 'value', html)
										}
										modules={quillModules}
										formats={quillFormats}
										placeholder={t('contentBlog.enterBlockText')}
										className='h-64 mb-4'
									/>
								) : (
									<>
										<input
											id={`content-value-${index}`}
											type='text'
											value={item.value}
											onChange={e =>
												handleContentItemChange(index, 'value', e.target.value)
											}
											className='w-full p-2 border border-gray-300 rounded-md mb-2'
											placeholder='URL изображения или загрузите файл'
										/>
										<input
											type='file'
											accept='image/*'
											onChange={e => handleContentImageUpload(index, e)}
										/>
										{item.value && (
											<div className='mt-2'>
												<Image
													width={150}
													height={150}
													src={item.value}
													alt='Изображение блока'
													className='max-w-[150px] h-auto rounded-md'
												/>
											</div>
										)}
									</>
								)}
							</div>
						</div>
					))}

				<div className='flex gap-2 mt-4'>
					<button
						type='button'
						onClick={() => handleAddContentItem('text')}
						className='px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600'
					>
						Добавить текстовый блок
					</button>
					<button
						type='button'
						onClick={() => handleAddContentItem('image')}
						className='px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600'
					>
						Добавить блок изображения
					</button>
				</div>

				{error && <p className='text-red-500 mt-4'>{error}</p>}

				<div className='flex justify-end gap-4 mt-6'>
					<button
						type='button'
						onClick={onEditComplete}
						className='px-6 py-3 bg-gray-300 text-gray-800 rounded-md hover:bg-gray-400'
					>
						Отмена
					</button>
					<button
						type='submit'
						disabled={loading}
						className='px-6 py-3 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50'
					>
						{loading ? 'Сохранение...' : 'Сохранить изменения'}
					</button>
				</div>
			</form>
		</div>
	)
}
