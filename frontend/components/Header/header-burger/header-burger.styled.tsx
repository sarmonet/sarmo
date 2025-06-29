import { colors } from '@/utils'
import styled from '@emotion/styled'

export const HeaderBurgerWrapper = styled.div`
	cursor: pointer;
	padding: 10px 0;
	z-index: 9999;
	button {
		margin-left: 10px;
	}
`
export const BurgerHeader = styled.div`
	display: flex;
	align-items: center;
	justify-content: center;
	flex-wrap: wrap;
	column-gap: 30px;
	position: absolute;
	top: 10%;
`

export const HeaderBurgerBody = styled.div`
	position: fixed;
	max-height: 100%;
	overflow-y: hidden;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: ${colors.mainWhiteTextColor};
	z-index: 9998;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;

	div {
		color: ${colors.mainTextColor};
		font-size: 20px;
		margin-bottom: 10px;
	}
`
export const HeaderBurgerMain = styled.div`
	display: flex;
	flex-direction: row-reverse;
	gap: 50px;
	a {
		display: flex;
		align-items: center;
		font-size: 21px;
		column-gap: 10px;
	}
`
export const HeaderBurgerList = styled.ul`
	display: flex;
	flex-direction: column;
	align-items: center;
	column-gap: 30px;
`
export const HeaderBurgerDrop = styled.div`
	transform: translateY(15px);
`
export const HeaderBurgerActions = styled.div`
	display: flex;
	flex-direction: column;
	gap: 25px;
`
