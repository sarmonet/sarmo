import { colors } from '@/utils'
import styled from '@emotion/styled'
interface Props {
	bgc?: string
	color?: string
}

export const ProfileContentWrapper = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 24px;
	width: 100%;
	form {
		display: flex;
		flex-direction: column;
		row-gap: 32px;
	}
	label {
		font-weight: 600;
		font-size: 14px;
		line-height: 24px;
	}
	input {
		width: 100%;
		padding: 12px;
		border-radius: 12px;
		background-color: #f4f4f4;
		color: #33383f;
		outline: none;
		margin-top: 14px;
	}

	@media (max-width: 768px) {
		padding: 0 15px;
	}
`
export const ProfileContentBlock = styled.div`
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	align-items: center;
	width: 100%;
	gap: 50px;
	@media (max-width: 1280px) {
		grid-template-columns: repeat(1, 1fr);
	}
	@media (max-width: 768px) {
	}
`

export const ButtonBlock = styled.div<Props>`
	display: flex;
	column-gap: 24px;
	margin-top: 18px;
`
export const ProfileHandleButton = styled.button<Props>`
	background-color: ${({ bgc }) => bgc || colors.btnMainColor};
	border-radius: 12px;
	padding: 12px 20px;
	color: ${({ color }) => color || colors.mainTextColor};
	font-weight: 700;
	font-size: 15px;
	line-height: 24px;
	cursor: pointer;
	transition: all 0.3s;
	&:hover {
		box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
	}
`
export const ProfileTitle = styled.h3<Props>`
	position: relative;
	font-weight: 600;
	font-size: 20px;
	line-height: 32px;
	padding-left: 25px;
	&:before {
		content: '';
		position: absolute;
		left: -2%;
		top: 0;
		height: 100%;
		width: 16px;
		border-radius: 4px;
		background-color: ${({ bgc }) => bgc || 'tranparent'};
	}
`
