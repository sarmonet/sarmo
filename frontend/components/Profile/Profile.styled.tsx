import { colors } from '@/utils'
import styled from '@emotion/styled'

export const ProfileWrapper = styled.section`
	padding: 40px;
	background-color: ${colors.profileBgColor};
	width: 100%;
	height: calc(250px * 3);
	overflow: auto;
	border-radius: 12px;
	overflow-x: hidden;
	@media (max-width: 1280px) {
		padding: 15px;
	}
`
export const ProfileMain = styled.div`
	display: flex;
	column-gap: 48px;
	padding: 24px;
	border-radius: 8px;
	background-color: ${colors.mainWhiteTextColor};
	@media (max-width: 1280px) {
		flex-direction: column;
	}
	@media (max-width: 480px) {
		padding: 12px 0px;
	}
`
export const ProfileTitle = styled.h3`
	background-color: ${colors.mainWhiteTextColor};
`
