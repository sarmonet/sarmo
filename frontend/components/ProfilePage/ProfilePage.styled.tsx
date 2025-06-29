import { colors } from '@/utils'
import styled from '@emotion/styled'
export const ProfilePageWrapper = styled.div`
	display: flex;
	column-gap: 40px;
`
export const MobileMenuWrapper = styled.div`
	position: absolute;
	display: flex;
	justify-content: space-between;
	top: 0%;
	left: 0;
	width: 100%;
	height: 105%;
	z-index: 100;
`

export const MobileMenu = styled.div`
	background-color: ${colors.mainWhiteTextColor};
	padding: 20px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
	z-index: 11;
	width: 250px;
	height: 100%;
	position: absolute;
	top: 0;
	left: 0;
	border-radius: 8px;
	@media (max-width: 769px) {
		width: 75px;
	}
`

export const MenuContent = styled.div`
	position: absolute;
	top: 35%;
	left: 10%;
	transform: translateY(-50%);
	@media (max-width: 769px) {
		left: 30%;
	}
`
