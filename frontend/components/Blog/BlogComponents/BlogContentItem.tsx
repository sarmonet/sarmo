import { formatDate } from '@/components/Listing/ListingComponents/FormatDateComponent'
import Image from "next/legacy/image"
import { FC } from 'react'
import { IContentBlock } from '../ContentInterface/Content.interface'
import {
	BlogFooter,
	BlogImage,
	BlogItem,
	BlogMain,
} from './BlogComponents.styled'
interface BlogContentProps {
	content: IContentBlock
	onClick: () => void
}

export const BlogContentItem: FC<BlogContentProps> = ({ content, onClick }) => {
	return (
		<BlogItem onClick={onClick}>
			<BlogImage
				src={content.mainImage}
				alt='Картинка'
				width={240}
				height={240}
			/>
			<BlogMain>
				<h3>{content.title}</h3>
				<p>{content.description}</p>
			</BlogMain>
			<BlogFooter>
				<Image
					src={content.author.profilePictureUrl}
					alt='Аватар'
					width={45}
					height={45}
					style={{ maxWidth: '45px', maxHeight: '45px', borderRadius: '50%' }}
				/>
				<div>
					<h4>
						{content.author.firstName} {content.author.lastName}
					</h4>
					<span>{formatDate(content.publicationDate)}</span>
				</div>
			</BlogFooter>
		</BlogItem>
	)
}
