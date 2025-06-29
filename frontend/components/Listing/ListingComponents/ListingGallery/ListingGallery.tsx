import { useDevice } from '@/components/hooks/useDevice'
import { colors } from '@/utils'
import Image from 'next/image'
import { useState } from 'react'
import { FaArrowLeft, FaArrowRight } from 'react-icons/fa'
import GalleryModal from './GalleryModal'
interface ListingGalleryProps {
	images: string[]
	mainImage: string
	videoUrl: string
	title: string
	category?: string
}

export const ListingGallery = ({
	images,
	mainImage,
	videoUrl,
	title,
	category,
}: ListingGalleryProps) => {
	const [activeTab, setActiveTab] = useState<'photo' | 'video'>('photo')
	const [currentImageIndex, setCurrentImageIndex] = useState(0)
	const { isMobile, isDesktop } = useDevice()
	const allImages = [mainImage, ...images]
	const [isOpen, setIsOpen] = useState(false)
	const handleNext = () => {
		setCurrentImageIndex(prev => (prev + 1) % allImages.length)
	}

	const handlePrev = () => {
		setCurrentImageIndex(
			prev => (prev - 1 + allImages.length) % allImages.length
		)
	}

	return (
		<>
			<div
				style={{
					position: 'relative',
					width: '100%',
					height: '400px',
					borderRadius: '12px',
					overflow: 'hidden',
				}}
			>
				{activeTab === 'photo' ? (
					<>
						<Image
							src={allImages[currentImageIndex] || '/images/sarmo.png'}
							alt={title}
							fill
							style={{ objectFit: 'cover' }}
							onClick={() => setIsOpen(true)}
						/>
						<button
							style={{
								position: 'absolute',
								top: '50%',
								left: '10px',
								transform: 'translateY(-50%)',
								background: `${colors.mainWhiteTextColor}`,
								border: 'none',
								borderRadius: '50%',
								color: `${colors.mainTextColor}`,
								width: '44px',
								height: '44px',
								cursor: 'pointer',
								boxShadow: '0 2px 4px rgba(0, 0, 0, 0.2)',
							}}
							onClick={handlePrev}
						>
							<FaArrowLeft
								style={{ position: 'relative', left: '50%', translate: '-50%' }}
							/>
						</button>
						<button
							style={{
								position: 'absolute',
								top: '50%',
								right: '10px',
								transform: 'translateY(-50%)',
								background: `${colors.mainWhiteTextColor}`,
								border: 'none',
								borderRadius: '50%',
								color: `${colors.mainTextColor}`,
								width: '44px',
								height: '44px',
								cursor: 'pointer',
								boxShadow: '0 2px 4px rgba(0, 0, 0, 0.2)',
							}}
							onClick={handleNext}
						>
							<FaArrowRight
								style={{ position: 'relative', left: '50%', translate: '-50%' }}
							/>
						</button>
						<div
							style={{
								position: 'absolute',
								bottom: isDesktop ? '10px' : '90%',
								right: '15px',
								background: `${colors.mainWhiteTextColor}`,
								color: `${colors.mainTextColor}`,
								padding: '5px 10px',
								borderRadius: '18px',
								fontSize: '14px',
							}}
						>
							Фото {currentImageIndex + 1} из {allImages.length}
						</div>
					</>
				) : (
					<video
						controls
						src={videoUrl}
						style={{ width: '100%', height: '100%', objectFit: 'contain' }}
					/>
				)}

				<div
					style={{
						position: 'absolute',
						bottom: !isDesktop ? '88%' : '15px',
						left: !isDesktop ? (isMobile ? '25%' : '15%') : '50%',
						transform: 'translateX(-50%)',
						display: 'flex',
						borderRadius: '999px',
						overflow: 'hidden',
						border: '1px solid #ccc',
					}}
				>
					<button
						style={{
							padding: '8px 20px',
							backgroundColor: activeTab === 'photo' ? '#000' : '#fff',
							color: activeTab === 'photo' ? '#fff' : '#000',
							border: 'none',
							cursor: 'pointer',
						}}
						onClick={() => setActiveTab('photo')}
					>
						Фото
					</button>
					{videoUrl && (
						<button
							style={{
								padding: '8px 20px',
								backgroundColor: activeTab === 'video' ? '#000' : '#fff',
								color: activeTab === 'video' ? '#fff' : '#000',
								border: 'none',
								cursor: 'pointer',
							}}
							onClick={() => setActiveTab('video')}
						>
							Видео
						</button>
					)}
				</div>
			</div>
			{allImages.length > 0 && (
				<GalleryModal
					isOpen={allImages && isOpen}
					onClose={() => setIsOpen(false)}
					images={allImages}
					title={title}
					category={category}
				/>
			)}
		</>
	)
}
