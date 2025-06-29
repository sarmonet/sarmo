import footerLogo from '@/public/images/logos/logoFooter.svg'
import headerLogo from '@/public/images/logos/logoHeader.svg'
import Image from "next/legacy/image"
import { useRouter } from 'next/router'
import { FC } from 'react'
import { Logo } from './headerLogo.style'
interface ILogo {
	isWhite?: boolean
}
export const HeaderLogo: FC<ILogo> = ({ isWhite = false }) => {
	const router = useRouter()
	const handleNavigation = () => {
		router.push('/').catch(error => {
			console.error('Navigation error:', error)
		})
	}
	return (
		<Logo>
			<Image
				src={isWhite ? footerLogo : headerLogo}
				onClick={() => handleNavigation()}
				height={45}
				width={208}
				alt='logo'
			/>
		</Logo>
	)
}
