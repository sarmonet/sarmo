import { AdministrationArticles } from './AdminArticles/AdministrationArticles'
import { AdministrationAcc } from './AdministrationAcc/AdministrationAcc'
import { AdministrationAll } from './AdministrationAll/AdministrationAll'
import { AnalyticsDashboard } from './AdministrationAnalytics/AdministrationAnalytics'
import { AdministrationCreate } from './AdministrationCreate/AdministrationCreate'
import { AdministrationListings } from './AdministrationListings/AdministrationListings'
import { AdministrationPackaging } from './AdministrationPackaging/AdministrationPackaging'
import { AdministrationSubscribe } from './AdministrationSubscribe/AdministrationSubscribe'
import { AdministrationTransaction } from './AdministrationTransaction/AdministrationTransaction'
export const AdministrationBody = ({ activeTab }: { activeTab: number }) => {
	return (
		<div>
			{activeTab === 0 && (
				<div>
					<AdministrationListings />
				</div>
			)}
			{activeTab === 1 && (
				<div>
					<AdministrationAll />
				</div>
			)}
			{activeTab === 2 && (
				<div>
					<AnalyticsDashboard />
				</div>
			)}
			{activeTab === 3 && (
				<div>
					<AdministrationSubscribe />
				</div>
			)}
			{activeTab === 4 && (
				<div>
					<AdministrationAcc />
				</div>
			)}
			{activeTab === 5 && (
				<div>
					<AdministrationTransaction />
				</div>
			)}
			{activeTab === 6 && (
				<div>
					<AdministrationPackaging />
				</div>
			)}
			{activeTab === 7 && (
				<div>
					<AdministrationCreate />
				</div>
			)}
			{activeTab === 8 && (
				<div>
					<AdministrationArticles />
				</div>
			)}
		</div>
	)
}
