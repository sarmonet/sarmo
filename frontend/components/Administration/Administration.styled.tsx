import { colors } from '@/utils'
import styled from '@emotion/styled'

interface AdminTabProps {
	activeTab: number
	index: number
}

export const AdministrationWrapper = styled.section`
	display: flex;
	flex-direction: column;
	row-gap: 30px;
`

export const AdminTabs = styled.ul`
	display: flex;
	justify-content: center;
	column-gap: 15px;
	align-items: center;
`

export const AdminTab = styled.li<AdminTabProps>`
	padding: 10px 0;
	cursor: pointer;
	border-bottom: ${({ activeTab, index }) =>
		activeTab === index ? `2px solid ${colors.btnMainColor}` : 'none'};
	color: ${({ activeTab, index }) =>
		activeTab === index ? colors.btnMainColor : colors.SecondGreyTextColor};
	font-weight: ${({ activeTab, index }) =>
		activeTab === index ? 'bold' : 'normal'};
	transition: background-color 0.3s ease, color 0.3s ease,
		border-bottom 0.3s ease;
	font-size: 18px;
	&:hover {
		background-color: ${colors.profileBgColor};
		color: ${colors.btnMainColor};
	}
`
