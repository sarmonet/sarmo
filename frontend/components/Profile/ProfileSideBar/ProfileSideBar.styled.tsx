import { colors } from '@/utils'
import styled from '@emotion/styled'

export const ProfileSideBarItems = styled.ul`
	display: flex;
	flex-direction: column;
	row-gap: 8px;
	width: 280px;
	color: ${colors.greyTextColor};
	flex-wrap: wrap;

	@media (max-width: 1280px) {
		flex-direction: row;
		width: 100%;
		margin: 5px 0 20px 0;
	}
	@media (max-width: 768px) {
		justify-content: space-around;
		align-items: center;
	}
`
export const ProfileSideBarItem = styled.li`
	width: 100%;
	min-width: 230px;
	padding: 8px 0 8px 16px;
	border-radius: 8px;
	cursor: pointer;
	transition: all 0.3s ease 0s;
	font-weight: 600;
	font-size: 16px;
	line-height: 24px;
	.active {
		background-color: ${colors.hoverLinkColor};
	}
	&:hover {
		color: ${colors.mainTextColor};
		background-color: ${colors.hoverLinkColor};
	}
	@media (max-width: 1280px) {
		width: fit-content;
		min-width: unset;
		padding: 8px 16px;
	}
`
