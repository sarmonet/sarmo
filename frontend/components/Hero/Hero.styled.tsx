import styled from '@emotion/styled'
import { colors, mq } from '../../utils'

export const HeroWrapper = styled.section`
	padding: 80px 60px;
	text-align: center;
	background: ${colors.blueHeroBack};
`

export const HeroContent = styled.div`
	display:flex;
	flex-direction: column;
	row-gap:40px;

	form{
		display: flex;
		justify-content: center;
		flex-wrap: wrap;
		column-gap:12px;
		row-gap: 30px;
	}
	input , select{
		width:100%;
		border-radius: 180px;
		padding: 15px 20px;
		outline: none;
		border-color: transparent;
		appearance: none; 

	}
}
	@media (max-width: 768px) {
		form{
			display: grid;
			grid-template-columns: repeat(2, 1fr);
			justify-content: center;
		}
		
	}
	@media (max-width: 665px) {
		
		form{
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			
		}
		
	}
	
	
`
export const HeroFilter = styled.div`
	display: flex;
	column-gap: 20px;
	z-index: 9;

	${mq.desktop} {
	}
`
export const HeroHat = styled.div`
	display: block;
	margin: 0 auto;
`
export const SearchBtn = styled.button`
	padding: 15px 20px;
	color: ${colors.mainWhiteTextColor};
	font-size: 18px;
	font-weight: 500;
	background-color: ${colors.btnSecondColor};
	border-radius: 180px;
`
