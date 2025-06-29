import { Container } from '../Container/Container'
import { HeaderActions } from './header-actions/headerActions'
import { HeaderLogo } from './header-logo/headerLogo'
import HeaderMenu from './header-menu/HeaderMenu'
import { HeaderBody, HeaderWrapper } from './Header.style'
export const Header = () => {
	return(
		<Container>
			<HeaderWrapper>
				<HeaderBody>
					<HeaderLogo />
					<HeaderMenu />
					<HeaderActions />
				</HeaderBody>
			</HeaderWrapper>	
		</Container>

	)
}