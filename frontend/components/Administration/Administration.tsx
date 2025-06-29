import withAuth from '@/hoc/withAuth'
import { useEffect, useState } from 'react'
import { AdministrationWrapper } from './Administration.styled'
import { AdministrationBody } from './AdministrationComponents/AdministrationBody'
import { AdministrationTabs } from './AdministrationComponents/AdministrationTabs'

const Administration = () => {
	const [activeTab, setActiveTab] = useState(0)
	useEffect(() => {
		sessionStorage.setItem('previousPath', window.location.pathname)
	}, [])
	return (
		<AdministrationWrapper>
			<AdministrationTabs setActiveTab={setActiveTab} activeTab={activeTab} />
			<AdministrationBody activeTab={activeTab} />
		</AdministrationWrapper>
	)
}
export default withAuth(Administration)
