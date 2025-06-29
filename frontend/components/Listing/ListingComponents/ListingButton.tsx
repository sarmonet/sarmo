import { FC } from 'react'
import { ListingButtonWrapper } from './ListingButton.styled'
interface IButtonProps {
	title: string
	image: React.ReactNode
	bgcolor: string
	color: string
	border: string
	onClick?: () => void
}

export const ListingButton: FC<IButtonProps> = ({
	title,
	image,
	onClick,
	bgcolor,
	border,
	color,
}) => {
	return (
		<ListingButtonWrapper
			border={border}
			bgcolor={bgcolor}
			onClick={onClick}
			color={color}
		>
			{title} {image}
		</ListingButtonWrapper>
	)
}
