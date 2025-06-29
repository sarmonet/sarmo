import { colors, mq } from '@/utils'
import styled from '@emotion/styled'

export const DropWrapper = styled.div`
	display: flex;
	flex-wrap: nowrap;
	position: relative;

	// min-width: 160px;
	cursor: pointer;
`
export const DropHeader = styled.div<{
	isRounded: boolean
	width: string
	isBorder: boolean
	bgc: string
}>`
	width: ${({ width }) => (width ? width : '210px')};
	background-color: ${({ bgc }) => (bgc ? bgc : colors.mainWhiteTextColor)};
	border-radius: ${({ isRounded }) => (isRounded ? '180px' : '16px')};
	border: ${({ isBorder }) =>
		isBorder ? `1px solid  ${colors.borderColor}` : 'transparent'};
	//width: 191px;
	padding: 12px 20px;
	display: flex;
	justify-content: space-between;
	column-gap: 10px;
	align-items: center;
	.rotate {
		transform: rotate(180deg);
		transition: all 0.3s ease;
	}
	${mq.tablet} {
		font-weight: 500;
		font-size: 16px;
		line-height: 21px;
	}
	${mq.largeDesktop} {
		font-weight: 500;
		font-size: 18px;
		line-height: 26px;
	}
`
export const DropList = styled.ul<{ isRounded: boolean }>`
	position: absolute;
	top: 100%;
	left: 0%;
	width: 100%;
	max-height: 250px;
	background-color: ${colors.mainWhiteTextColor};
	border-radius: ${({ isRounded }) =>
		isRounded ? '16px' : '0px 0px 16px 16px'};
	z-index: 1000;
	padding: 10px;
	overflow-y: auto;
	box-shadow: 0px 4px 20px rgba(0, 0, 0, 0.1);
	border: 1px solid ${colors.borderColor};
	li {
		font-weight: 500;
		font-size: 18px;
		line-height: 26px;
		padding: 12px 20px;
		cursor: pointer;
		transition: 0.3s ease 0s;

		&:hover {
			color: ${colors.btnMainColor};
			background-color: ${colors.hoverLinkColor};
			border-radius: 16px;
		}
	}
`
