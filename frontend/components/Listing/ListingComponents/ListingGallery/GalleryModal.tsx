import Image from 'next/image'
import { useEffect, useState } from 'react'
import { createPortal } from 'react-dom'
import { MdKeyboardArrowLeft, MdKeyboardArrowRight } from 'react-icons/md'
interface GalleryModalProps {
	isOpen: boolean
	onClose: () => void
	images: string[]
	title: string
	category?: string
}

const GalleryModal = ({
	isOpen,
	onClose,
	images,
	title,
	category,
}: GalleryModalProps) => {
	const [currentIndex, setCurrentIndex] = useState(0)
	const [mounted, setMounted] = useState(false)

	useEffect(() => {
		setMounted(true)
		if (isOpen) setCurrentIndex(0)
	}, [isOpen])

	if (!isOpen || !mounted) return null

	const handlePrev = () => {
		setCurrentIndex(prev => (prev - 1 + images.length) % images.length)
	}

	const handleNext = () => {
		setCurrentIndex(prev => (prev + 1) % images.length)
	}

	return createPortal(
		<div
			className='fixed inset-0 z-[1000] bg-black bg-opacity-80 flex items-center justify-center p-[20px]'
			onClick={onClose}
		>
			<div
				className='relative max-w-5xl w-full bg-white rounded-[28px] p-4'
				onClick={e => e.stopPropagation()}
			>
				<button
					className='absolute top-0 right-4 text-[24px]'
					onClick={() => {
						onClose()
					}}
				>
					X
				</button>
				<h3 className='flex justify-center pb-[20px] text-[24px]'>
					{title} || {category}
				</h3>
				<div className='relative w-full flex items-center justify-center h-[500px] mb-[60px]'>
					<button
						onClick={handlePrev}
						className='absolute left-0 z-10 bg-white rounded-full p-2 shadow-md hover:bg-gray-100'
					>
						<MdKeyboardArrowLeft size={24} />
					</button>

					<Image
						fill={true}
						src={images[currentIndex] || '/images/sarmo.png'}
						alt={`image-${currentIndex}`}
						className='max-h-full object-contain rounded-xl '
					/>

					<button
						onClick={handleNext}
						className='absolute right-0 z-10 bg-white rounded-full p-2 shadow-md hover:bg-gray-100'
					>
						<MdKeyboardArrowRight size={24} />
					</button>

					<div className='absolute bottom-[-8%] right-[50%] bg-white px-3 py-1 rounded-full text-sm text-gray-800 shadow-md'>
						{currentIndex + 1}/{images.length}
					</div>
				</div>

				<div className='mt-6  overflow-x-auto no-scrollbar'>
					<div className='flex gap-2 justify-center'>
						{images.map((src, index) => (
							<Image
								width={80}
								height={80}
								alt='altPhote'
								key={index}
								src={src || '/images/sarmo.png'}
								onClick={() => setCurrentIndex(index)}
								className={`h-20 cursor-pointer rounded border-2 ${
									index === currentIndex ? 'border-black' : 'border-transparent'
								}`}
							/>
						))}
					</div>
				</div>
			</div>
		</div>,
		document.body
	)
}

export default GalleryModal
