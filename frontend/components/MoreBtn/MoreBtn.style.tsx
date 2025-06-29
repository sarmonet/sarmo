import { colors } from '@/utils'
import styled from '@emotion/styled'
interface props {
	bgcolor?: string
	color?: string
}
export const MoreButton = styled.button<props>`
	display: flex;
	align-items: center;
	justify-content: center;
	column-gap: 5px;
	padding: 15px 75px;
	border-radius: 180px;
	background-color: ${({ bgcolor }) =>
		bgcolor ? bgcolor : colors.btnMainColor};
	color: ${({ color }) => (color ? color : colors.mainWhiteTextColor)};
	font-size: clamp(14px, 3vw, 18px);
	line-height: 26px;
	font-weight: 500;
	min-width: 100%;
	border: ${({ bgcolor }) =>
		bgcolor ? `1px solid ${colors.borderColor}` : 'none'};
	&:hover {
		box-shadow: 0px 4px 20px rgba(0, 0, 0, 0.1);
	}
	@media (max-width: 768px) {
		padding: 13px 55px;
	}
	@media (max-width: 488px) {
		padding: 13px 25px;
	}
`
