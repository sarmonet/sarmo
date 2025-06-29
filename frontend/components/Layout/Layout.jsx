import { Header } from '@/components/Header/Header'
import { Toaster } from 'react-hot-toast'
import { Footer } from '../Footer/Footer'
import { Main, Wrapper } from './Layout.styled'
export const metadata = {
	title: 'Sarmo - Купить и продать готовый бизнес и бизнес-идеи',
	description: 'Sarmo - ведущая платформа для купли-продажи готового бизнеса, франшиз и инновационных бизнес-идей. Найдите инвестиции или представьте свой проект.',
	keywords: ['купить бизнес', 'продать бизнес', 'готовый бизнес', 'франшиза', 'бизнес идеи', 'инвестиции в бизнес', 'стартапы', 'покупка бизнеса', 'продажа бизнеса'],
  authors: [{ name: 'Sarmo' }],
  robots: 'index, follow',
}

export default function ClientLayout({ children }) {
	return (
		<Wrapper>
			<Header />
			<Main>{children}</Main>
			<Toaster />
			{window.location.pathname === '/chat' ? null : <Footer />}
	</Wrapper>
	)
}
