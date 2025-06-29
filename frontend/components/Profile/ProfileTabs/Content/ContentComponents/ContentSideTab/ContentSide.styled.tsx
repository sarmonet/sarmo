import { colors } from '@/utils'
import styled from '@emotion/styled'
export const DescriptionBlock = styled.label`
	display: flex;
	flex-direction: column;
	row-gap: 5px;
	textarea {
		border: 1px solid #ccc;
		border-radius: 5px;
		padding: 5px;
	}
`

export const AdditionalBlock = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 25px;
`
export const AdditionalType = styled.div`
	display: flex;
	align-items: center;
	column-gap: 5px;
	font-weight: 700;
	font-size: 14px;
	height: 70px;
	cursor: pointer;
	margin-top: 20px;
	select {
		border: 1px solid #ccc;
		border-radius: 5px;
		padding: 5px;
	}
`
export const DescriptionButton = styled.button`
	width: fit-content;
	padding: 10px 20px;
	margin-top: 10px;
	background-color: ${colors.btnSecondColor};
	color: ${colors.mainWhiteTextColor};
	font-size: 18px;
	border-radius: 8px;
	cursor: pointer;
`

export const AddButton = styled.button`
	color: ${colors.btnMainColor};
`
