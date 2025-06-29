import { useTranslation } from 'next-i18next'
import React, { useEffect, useRef } from 'react'
import { BsTrash } from 'react-icons/bs'
interface ContextMenuProps {
	x: number
	y: number
	onDelete: () => void
	onClose: () => void
}

export const ContextMenu: React.FC<ContextMenuProps> = ({
	x,
	y,
	onDelete,
	onClose,
}) => {
	const menuRef = useRef<HTMLUListElement>(null)
	const { t } = useTranslation('common')

	useEffect(() => {
		const handleClickOutside = (event: MouseEvent) => {
			if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
				onClose()
			}
		}

		document.addEventListener('mousedown', handleClickOutside)
		return () => {
			document.removeEventListener('mousedown', handleClickOutside)
		}
	}, [onClose])

	return (
		<ul
			ref={menuRef}
			style={{
				top: y,
				left: x,
				position: 'absolute',
				background: 'white',
				border: '1px solid #ccc',
				borderRadius: '4px',
				padding: '5px 0',
				zIndex: 1000,
				boxShadow: '0 2px 5px rgba(0,0,0,0.2)',
			}}
			className='dark:bg-gray-700 dark:border-gray-600 dark:text-white'
		>
			<li
				onClick={() => {
					onDelete()
					onClose()
				}}
				style={{
					display: 'flex',
					alignItems: 'center',
					gap: '8px',
					padding: '8px 12px',
					cursor: 'pointer',
					listStyle: 'none',
				}}
				className='text-[#007BFF] hover:bg-gray-100 dark:hover:bg-gray-600'
			>
				<BsTrash />
				{t('chatMain.deleteMessageOption')}
			</li>
		</ul>
	)
}
