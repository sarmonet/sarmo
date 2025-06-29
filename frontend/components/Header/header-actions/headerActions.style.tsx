import { colors, mq } from '@/utils'
import styled from '@emotion/styled'

export const Actions = styled.div`
	display: flex;
	align-items: center;
	justify-content: space-between;
	${mq.tablet} {
		column-gap: 5px;
	}

	${mq.desktop} {
	}
	${mq.largeDesktop} {
		column-gap: 15px;
	}
`
export const HeaderAction = styled.button<{ isVip?: boolean }>`
	background-color: ${({ isVip }) =>
		isVip ? colors.btnSecondColor : colors.btnMainColor};
	border-radius: 180px;
	color: ${colors.mainWhiteTextColor};
	transition: 0.3s ease 0s;
	z-index: 1000;
	min-width: fit-content;
	&:hover {
		opacity: 0.7;
	}
	${mq.smallMobile} {
		font-size: 14px;
		font-weight: 400;
		line-height: 21px;
		padding: 10px 12px;
	}

	${mq.desktop} {
	}
	${mq.largeDesktop} {
		font-size: 18px;
		font-weight: 500;
		line-height: 26px;
		padding: 15px 20px;
	}
`

export const HeaderMain = styled.div`
	position: relative;
	display: flex;
	align-items: center;
	column-gap: 12px;

	svg {
		cursor: pointer;
		transition: 0.3s ease 0s;
		&:hover {
			transform: scale(1.1);
		}
	}

	${mq.tablet} {
		margin-left: 15px;
	}

	${mq.desktop} {
	}
	${mq.largeDesktop} {
		margin-left: 65px;
	}
`
export const DropDown = styled.div`
	position: absolute;
	top: 150%;
	width: 306px;
	right: -0%;
	padding: 20px 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	border-bottom-right-radius: 16px;
	border-bottom-left-radius: 16px;
	background-color: ${colors.mainWhiteTextColor};
	z-index: 2000;
	row-gap: 20px;
	box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.1);
	button {
		width: 90%;
	}

	${mq.tablet} {
		margin-left: 15px;
	}

	${mq.desktop} {
	}
	${mq.largeDesktop} {
		margin-left: 65px;
	}
`
