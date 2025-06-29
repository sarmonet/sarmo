import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import Link from 'next/link'
import { FC } from 'react'
import { IMenuLink } from './menu-item.interface'
interface IMenuItem {
	item: IMenuLink
}

const MenuItem: FC<IMenuItem> = ({ item }) => {
	const { setActiveCategory } = useCatalog()
	return (
		<li>
			<Link
				href={item.link}
				onClick={
					item.name === 'Каталог' ? () => setActiveCategory(null) : undefined
				}
			>
				{item.name}
			</Link>
		</li>
	)
}

export default MenuItem
