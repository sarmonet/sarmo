import { postSettings } from '@/services/support'
import { useState } from 'react'
import { ClosePop, ListingItem, ListingSupport, ListingSupportWrapper, Submit } from './ListingSupport.style'

interface ListingSupportPopProps {
  setIsSupportVisible: (isVisible: boolean) => void;
  listingId: number;
}

export const ListingSupportPop = ({ setIsSupportVisible, listingId }: ListingSupportPopProps) => {
  const [supportState, setSupportState] = useState({
    negotiationsAndAnalysis: false,
    preliminaryContractConclusion: false,
    businessDueDiligence: false,
    financialAnalysis: false,
    financialPlanDevelopment: false,
    mainContractConclusion: false,
    postDealSupport: false,
  });

  const handleCheckboxChange = (key: keyof typeof supportState) => {
    setSupportState((prevState) => ({
      ...prevState,
      [key]: !prevState[key],
    }));
  };

  const handleSubmit = async () => {
    try {
      await postSettings({ listingId }, supportState);
      setIsSupportVisible(false);
    } catch (error) {
      console.error('Ошибка при отправке настроек:', error);
    }
  };
  
  
  

const supportOptions: { key: keyof typeof supportState; label: string; list: string[] }[] = [
  {
    key: 'negotiationsAndAnalysis',
    label: 'Переговоры и анализ',
    list: [
      'Анализ документов, предоставленных продавцом.',
      'Оценка рыночной стоимости бизнеса.',
      'Согласование условий сделки.',
      'Подготовка предварительных финансовых расчётов',
    ],
  },
  {
    key: 'preliminaryContractConclusion',
    label: 'Заключение предварительного договора',
    list: [
      'Фиксация ключевых условий сделки.',
      'Внесение обеспечительного платежа в условное депонирование (эскроу-счёт).',
      'Открытие доступа к внутренней документации компании.',
    ], 
  },
  {
    key: 'financialAnalysis',
    label: 'Финансовая модель',
     list: [
      'Сбор и верификация исходных финансовых данных.',
      'Разработка структуры финансовой модели.',
      'Прогнозирование будущих финансовых потоков.',
      'Проведение анализа чувствительности и стресс-тестирования.',
      'Оценка стоимости бизнеса по модели.',
      'Подготовка презентации финансовой модели.',
    ],
  },
  
  {
    key: 'businessDueDiligence',
    label: 'Проверка бизнеса (Due Diligence)',
    list: [
      'Финансовый и юридический аудит.',
      'Анализ договоров с контрагентами.',
      'Оценка налоговых обязательств.',
      'Проверка рисков и составление рекомендаций.',
    ],
  },
  {
    key: 'financialPlanDevelopment',
    label: 'Разработка финансового плана',
    list: [
      'Анализ доходов, расходов, структуры капитала.',
      'Определение точек роста прибыли.',
      'Подготовка стратегии по оптимизации финансовых потоков.',
      'Финансовая модель.'
    ],
  },
  {
    key: 'mainContractConclusion',
    label: 'Заключение основного договора',
    list: [
     ' Окончательное согласование условий.',
      'Подписание договора.',
      'Проведение расчетов между сторонами.',
      'Передача прав собственности'
    ], 
  },
  {
    key: 'postDealSupport', 
    label: 'Пост-сделочное сопровождение (Дополнительный доход)',
    list: [
     ' Разработка стратегии по интеграции бизнеса.',
      'Оптимизация налоговой нагрузки.',
      'Оценка персонала и кадровые рекомендации.',
      'Консультации по управлению новым активом.',
      'Бизнес-план.'
    ],
  },
];


  return (
    <ListingSupportWrapper>
      <ListingSupport>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <h2>Сопровождение проекта</h2>
          <ClosePop onClick={() => setIsSupportVisible(false)}>X</ClosePop>
        </div>
        <ul>
          {supportOptions.map((option) => (
            <ListingItem key={option.key}>
              <div style={{ display: 'flex', alignItems: 'center', columnGap: '15px' }}>
                <input
                  type="checkbox"
                  checked={supportState[option.key]}
                  onChange={() => handleCheckboxChange(option.key)}
                  style={{cursor: 'pointer' ,minHeight: '20px', minWidth: '20px'}}
                />
                
                <span>{option.label}</span>
              </div>
              {supportState[option.key] && option.list.map((item, index) => (
                  <ol key={index} style={{ marginLeft: '20px' }}>
                    <li>{item}</li> 
                  </ol>
                ))}
            </ListingItem>
          ))}
        </ul>
        <Submit onClick={handleSubmit}>Подтвердить</Submit>
      </ListingSupport>
    </ListingSupportWrapper>
  );
};