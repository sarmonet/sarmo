import { colors } from '@/utils'
import styled from '@emotion/styled'

export const SideItems = styled.ul`
	position: relative;
	display: flex;
	flex-direction: column;
	gap: 15px;
	width: clamp(250px, 0px, 340px);
	margin-top: 30px;
	@media (max-width: 768px) {
		width: 100%;
	}
`
export const SideItem = styled.li`
	display: flex;
	width: 100%;
	align-items: center;
	column-gap: 15px;
	font-weight: 600;
	font-size: 18px;
	line-height: 24px;
	color: ${colors.greyTextColor};
	cursor: pointer;
	transition: all 0.3s ease 0s;
	&:hover {
		transform: translateX(10%);
		color: ${colors.btnMainColor};
	}
	@media (max-width: 768px) {
		span {
			display: none;
		}
		svg {
			width: 30px;
			height: 30px;
		}
	}
`
export const LogOut = styled.button`
	position: absolute;
	display: flex;
	align-items: center;
	column-gap: 15px;
	bottom: 0;
	display: flex;
	width: fit-content;
	align-items: center;
	column-gap: 15px;
	font-weight: 500;
	font-size: 21px;
	text-transform: uppercase;
	letter-spacing: 1.3px;
	cursor: pointer;
	padding: 7px 10px;
	transition: all 0.3s ease 0s;
	&:hover {
		color: #c51b1b;
	}
	@media (max-width: 880px) {
		bottom: -30%;
		left: -5%;
	}
	@media (max-width: 768px) {
		left: -30%;
		span {
			display: none;
		}
		svg {
			width: 30px;
			height: 30px;
		}
	}
	@media (max-width: 490px) {
		position: relative;
		top: 0;
		left: -20%;
		svg {
			color: ${colors.errorColor};
		}
	}
`
export const MobileMenuWrapper = styled.div`
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0.5);
	z-index: 10;
	display: flex;
	justify-content: flex-start;
	align-items: flex-start;
`

export const MobileMenu = styled.div`
	background-color: ${colors.mainWhiteTextColor};
	padding: 20px;
	border-radius: 8px;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
	z-index: 11;
`
