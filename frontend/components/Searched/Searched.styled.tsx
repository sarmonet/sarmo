import { colors } from '@/utils'
import styled from '@emotion/styled'
export const SearchedWrapper = styled.section`
	padding: 60px 0;

		@media (max-width: 1440px) {
			width: 1280px;
	}
  @media (max-width: 1024px) {
		width: 960px;
	}
  @media (max-width: 768px) {
		width: 630px;
	}
  @media (max-width: 480px) {
		width: 386px;
	}
`
export const SearchedList = styled.ul`
	display: grid;
	align-items: center;
	grid-template-columns: repeat(auto-fill, minmax(300px, 400px));
	gap: 40px;

`
export const SearchedItem = styled.li`
	display: flex;
	align-items: center;
	gap: 20px;
	cursor: pointer;
	padding: 16px;
	border-radius: 16px;
	border: 1px solid ${colors.borderColor};
	h3{
		font-size: 28px;
		line-height: 38px;
		font-weight: 500;
	}
	p{
		font-size: 18px;
		line-height: 26px;
		color: ${colors.subtitleTextColor};
	}
	&:hover {
		color: ${colors.btnMainColor};
	}
	img{
		width: 76px;
		height: 76px;
		object-fit: cover;
		border-radius: 8px;
		object-position: 10% 100%;
	}
`


