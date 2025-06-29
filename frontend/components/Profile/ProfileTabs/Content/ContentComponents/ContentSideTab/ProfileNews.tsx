'use client'

import {
	IContent,
	IContentItem,
} from '@/components/Blog/ContentInterface/Content.interface'
import { postNews } from '@/services/getContent'
import { postImage } from '@/services/uploadFiles'
import { useTranslation } from 'next-i18next'
import dynamic from 'next/dynamic'
import React, { useMemo, useState } from 'react'
import toast from 'react-hot-toast'
import {
	AddButton,
	AdditionalBlock,
	AdditionalType,
	DescriptionBlock,
	DescriptionButton,
} from './ContentSide.styled'

const ReactQuill = dynamic(() => import('react-quill'), { ssr: false })

export const ProfileNews = () => {
	const { t } = useTranslation('common')

	const [title, setTitle] = useState('')
	const [description, setDescription] = useState('')
	const [content, setContent] = useState<IContentItem[]>([
		{ type: 'text', title: '', value: '' },
	])
	const [selectedFile, setSelectedFile] = useState<File | null>(null)
	const [imageUrl, setImageUrl] = useState('')
	const [error, setError] = useState('')

	const quillModules = useMemo(
		() => ({
			toolbar: [
				[{ header: [1, 2, 3, false] }],

				['bold', 'italic', 'underline', 'strike'],
				[{ list: 'ordered' }, { list: 'bullet' }],
				['link'],
				['clean'],
			],
		}),
		[]
	)

	const quillFormats = [
		'header',

		'bold',
		'italic',
		'underline',
		'strike',
		'list',
		'bullet',
		'link',
	]

	const handlePostNews = async (e: React.FormEvent) => {
		e.preventDefault()
		setError('')

		if (!imageUrl && !selectedFile) {
			setError('uploadImageError')
			return
		}

		const data: IContent = {
			mainImage: imageUrl || '',
			title,
			description,
			content,
		}

		try {
			await postNews(data)

			toast.success('Новость успешно создан!')
		} catch (err) {
			console.error('Ошибка при отправке новости:', err)
			setError('submitError')
		}
	}

	const handleContentChange = (
		index: number,
		field: keyof IContentItem,
		value: string
	) => {
		const newContent = [...content]
		newContent[index][field] = value
		setContent(newContent)
	}

	const addContentItem = () => {
		setContent([...content, { type: 'text', title: '', value: '' }])
	}

	const handleFileChange = async (
		event: React.ChangeEvent<HTMLInputElement>
	) => {
		if (event.target.files?.[0]) {
			const file = event.target.files[0]
			setSelectedFile(file)

			try {
				const imageData = await postImage(file)
				setImageUrl(imageData)
			} catch (err) {
				console.error('Ошибка при загрузке изображения:', err)
				setError('imageUploadError')
			}
		}
	}

	const handleContentImageChange = async (
		index: number,
		event: React.ChangeEvent<HTMLInputElement>
	) => {
		if (event.target.files?.[0]) {
			const file = event.target.files[0]

			try {
				const imageData = await postImage(file)
				const newContent = [...content]
				newContent[index].value = imageData
				setContent(newContent)
			} catch (err) {
				console.error('Ошибка при загрузке изображения:', err)
				setError('imageUploadError')
			}
		}
	}

	return (
		<div>
			<form onSubmit={handlePostNews}>
				<label>
					{t('contentBlog.titleLabel')}:
					<input
						type='text'
						value={title}
						onChange={e => setTitle(e.target.value)}
					/>
				</label>
				<DescriptionBlock>
					{t('contentBlog.descriptionLabel')}:
					<textarea
						value={description}
						onChange={e => setDescription(e.target.value)}
					/>
				</DescriptionBlock>
				<label>
					{t('contentBlog.uploadImage')}:
					<input type='file' accept='image/*' onChange={handleFileChange} />
					{selectedFile && <span>{selectedFile.name}</span>}
				</label>
				<div>
					{content.map((item, index) => (
						<div key={index}>
							<AdditionalType>
								<span>{t('contentBlog.blockType')}:</span>
								<select
									value={item.type}
									onChange={e =>
										handleContentChange(index, 'type', e.target.value)
									}
								>
									<option value='text'>{t('contentBlog.typeText')}</option>
									<option value='image'>{t('contentBlog.typeImage')}</option>
								</select>
							</AdditionalType>
							<AdditionalBlock>
								<label
									style={{
										display: 'flex',
										flexDirection: 'column',
										rowGap: '5px',
									}}
								>
									{t('contentBlog.blockTitle')}:
									<input
										type='text'
										value={item.title}
										onChange={e =>
											handleContentChange(index, 'title', e.target.value)
										}
									/>
								</label>
								{item.type === 'text' && (
									<>
										<DescriptionBlock
											style={{
												display: 'flex',
												flexDirection: 'column',
												rowGap: '5px',
											}}
										>
											{t('contentBlog.blockDescription')}:
										</DescriptionBlock>
										<ReactQuill
											key={`news-editor-${index}`}
											value={item.value}
											theme='snow'
											onChange={html =>
												handleContentChange(index, 'value', html)
											}
											modules={quillModules}
											formats={quillFormats}
											placeholder={t('contentBlog.enterBlockText')}
											className='h-64 mb-4'
										/>
									</>
								)}
								{item.type === 'image' && (
									<label>
										{t('contentBlog.uploadImageBlock')}:
										<input
											type='file'
											accept='image/*'
											onChange={e => handleContentImageChange(index, e)}
										/>
									</label>
								)}
							</AdditionalBlock>
						</div>
					))}
				</div>
				<AddButton type='button' onClick={addContentItem}>
					{t('contentBlog.addItem')}
				</AddButton>
				{error && (
					<p style={{ color: 'red' }}>{t(`contentBlog.${error}`) || error}</p>
				)}
				<DescriptionButton type='submit'>
					{t('contentBlog.submit')}
				</DescriptionButton>
			</form>
		</div>
	)
}
