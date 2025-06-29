import { ISubscribe } from '@/components/Catalog/Catalog-data/CatalogInterface/Subscribe.interface'
import { delProfileSubs, getProfileSubs, putSubs } from '@/services/subscriptions'
import { colors } from '@/utils'
import FormControlLabel from '@mui/material/FormControlLabel'
import FormGroup from '@mui/material/FormGroup'
import Switch from '@mui/material/Switch'
import { useTranslation } from 'next-i18next'
import { useEffect, useState } from 'react'
import { MdDelete } from 'react-icons/md'
import { ProfileTitle } from '../ProfileContent.styled'
import {
  CategoryTitle,
  Delete,
  FiltersTextWrapper,
  NewBadge,
  ProfileNotificationHeader,
  ProfileNotificationItem,
  ProfileNotificationItems,
  ProfileNotificationSwitch,
  Save
} from './ProfileNotification.styled'

export const ProfileNotification = () => {
  const [subsContent, setSubsContent] = useState<ISubscribe[]>([]);
  const [subscriptionSettings, setSubscriptionSettings] = useState<{
    [id: number]: {
      method: string;
      active: boolean | undefined;
      frequency: 'DAILY' | 'WEEKLY' | 'MONTHLY' | undefined;
    };
  }>({});
  const { t } = useTranslation('common');
  const handleDelete = async (id: number) => {
    try {
      await delProfileSubs(id);
      setSubsContent((prev) => prev.filter((item) => item.id !== id));
      setSubscriptionSettings((prev) => {
        const newState = { ...prev };
        delete newState[id];
        return newState;
      });
    } catch (error) {
      console.error('Error deleting subscription:', error);
    }
  };

  useEffect(() => {
    const fetchProfileSubs = async () => {
      try {
        const response = await getProfileSubs();
        setSubsContent(response);
        const initialSettings: {
          [id: number]: {
            method: string;
            active: boolean | undefined;
            frequency: 'DAILY' | 'WEEKLY' | 'MONTHLY' | undefined;
          };
        } = {};

        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        response.forEach((sub : any) => {
          initialSettings[sub.id] = {
            method: sub.preferredCommunicationChannel || 'email',
            active: sub.categorySubscription?.active,
            frequency: sub.categorySubscription?.frequency,
          };
        });
        setSubscriptionSettings(initialSettings);
      } catch (error) {
        console.error('Error fetching profile data:', error);
      }
    };

    fetchProfileSubs();
  }, []);

  const handleMethodChange = (
    event: React.ChangeEvent<HTMLSelectElement>,
    id: number
  ) => {
    setSubscriptionSettings((prev) => ({
      ...prev,
      [id]: { ...prev[id], method: event.target.value },
    }));
  };

  const handleCheckboxChange = (id: number) => {
    setSubscriptionSettings((prev) => ({
      ...prev,
      [id]: { ...prev[id], active: !prev[id]?.active },
    }));
  };

  const handleFrequencyChange = (
    event: React.ChangeEvent<HTMLSelectElement>,
    id: number
  ) => {
    setSubscriptionSettings((prev) => ({
      ...prev,
      [id]: { ...prev[id], frequency: event.target.value as 'DAILY' | 'WEEKLY' | 'MONTHLY' },
    }));
  };

  const handleSettingsChange = async (id: number) => {
    const settings = subscriptionSettings[id];
    const sub = subsContent.find((s) => s.id === id);
    if (settings && sub) {
      const subscriptionChanged: ISubscribe = {
        id: id,
        preferredCommunicationChannel: settings.method as
          | 'EMAIL'
          | 'APPLICATION',
        categorySubscription: {
          active: settings.active,
          frequency: settings.frequency,
          filters: sub.categorySubscription?.filters, 
        },
      };
      try {
        await putSubs(subscriptionChanged);
        setSubsContent((prev) =>
          prev.map((s) =>
            s.id === id
              ? {
                  ...s,
                  preferredCommunicationChannel:
                    subscriptionChanged.preferredCommunicationChannel,
                  categorySubscription: {
                    ...s.categorySubscription,
                    active: subscriptionChanged.categorySubscription?.active,
                    frequency: subscriptionChanged.categorySubscription?.frequency,
                  },
                }
              : s
          )
        );
      } catch (error) {
        console.error('Error updating subscription:', error);
      }
    }
  };

  return (
    <>
      <ProfileTitle bgc={colors.btnSecondColor}>Подписки по фильтрам</ProfileTitle>
      <ProfileNotificationItems>
        {subsContent.map((item) => {
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          const filters = (item.categorySubscription?.filters as any)?.filteredParams;

          const communicationMethod = item.preferredCommunicationChannel;
          const settings = subscriptionSettings[item.id];
          const currentFrequency = item.categorySubscription?.frequency;
          const categoryName = filters?.sqlFilters.categoryName as string | undefined;

          if (!filters) return null;

          return (
            <ProfileNotificationItem key={item.id}>
              <ProfileNotificationHeader>
                <CategoryTitle>{categoryName}</CategoryTitle>
                <NewBadge>0 {t('profileNotification.countTitle')}</NewBadge>
              </ProfileNotificationHeader>
              <FiltersTextWrapper>
              {Object.entries(filters.sqlFilters || {})
                .filter(([key]) => key !== 'category' && key !== 'subCategory' && key !== 'categoryName') 
                .filter(([, value]) => value !== null && value !== undefined)
                .map(([, value], idx) => ( 
                  // eslint-disable-next-line @typescript-eslint/no-explicit-any
                  (<span key={`sql-${idx}`}>{value as any}</span>)
                ))}
             {Object.entries(filters.mongoFilters || {}).map(([key, value], idx) => {
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                const val = value as any;

                const displayValue = typeof val === 'object' && val?.min !== undefined && val?.max !== undefined
                  ? `от ${val.min} до ${val.max}`
                  : String(val);

                return (
                  <span key={`mongo-${idx}`}>
                    {key}: {displayValue}
                  </span>
                );
              })}

            </FiltersTextWrapper>
              <ProfileNotificationSwitch>
                <FormGroup style={{display: 'flex', alignItems: 'center'}} >
                    <FormControlLabel
                      control={<Switch checked={!!settings?.active} onChange={() => handleCheckboxChange(item.id)} />}
                      label={settings?.active ? t('profileNotification.labelOn') :  t('profileNotification.labelOff')}
                    />
                  </FormGroup>
                  <select
                    
                    onChange={(e) => handleFrequencyChange(e, item.id)}
                    value={settings?.frequency || currentFrequency}
                  >
                    <option value="DAILY"> {t('profileNotification.optionDayly')}</option>
                    <option value="WEEKLY">{t('profileNotification.optionWeekly')}</option>
                    <option value="MONTHLY">{t('profileNotification.optionMonthly')}</option>
                  </select>
                  <select
                  
                    onChange={(e) => handleMethodChange(e, item.id)}
                    value={settings?.method || 'email'}
                  >
                    <option value="EMAIL">{communicationMethod}</option>
                    <option value="APPLICATION">Application</option>
                  </select>
                  <Delete onClick={() => handleDelete(item?.id)}>
                    <MdDelete size={24} fill={`${colors.errorColor}`}/>
                  </Delete>
                  <Save onClick={() => handleSettingsChange(item.id)}>
                    {t('buttons.saveChangesBtn')}
                  </Save>
              </ProfileNotificationSwitch>
            </ProfileNotificationItem>
          );
        })}
      </ProfileNotificationItems>
    </>
  );
};