import { colors } from '@/utils'
import styled from '@emotion/styled'

export const ProfileWrapper = styled.section`
	padding: 40px;
	background-color: ${colors.profileBgColor};
	width: 100%;
`
export const ProfileMain = styled.div`
	display: flex;
	column-gap: 32px;
	padding: 24px;
	border-radius: 8px;
	background-color: ${colors.mainWhiteTextColor};
`
export const FavoriteItems = styled.div`
	display: grid;
	grid-template-columns: repeat(3, minmax(250px, 300px));
	align-items: center;
	justify-content: center;
	gap: 20px;
	@media (max-width: 1440px) {
		grid-template-columns: repeat(2, minmax(300px, 300px));
	}
	@media (max-width: 1024px) {
		grid-template-columns: repeat(1, minmax(300px, 300px));
	}
`
export const FavoriteItem = styled.div``
