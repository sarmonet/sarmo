import { useDevice } from '@/components/hooks/useDevice'
import { colors } from '@/utils'
import { Box, Input, VStack } from "@chakra-ui/react"
import { useEffect, useState } from "react"
import { Range, getTrackBackground } from "react-range"
interface RangeSliderProps {
  onFilterChange: (filterName: string, value: unknown, type?: string) => void;
}

export const RangeSlider = ({ onFilterChange }: RangeSliderProps) => {
  const STEP = 100000;
  const MIN = 1;
  const MAX = 10000000;

  const [values, setValues] = useState([1, 6000000]);
  const [inputValues, setInputValues] = useState(["1", "6 000 000"]);
  const { isDesktop } = useDevice();
  useEffect(() => {
    onFilterChange("minPrice", values[0]);
    onFilterChange("maxPrice", values[1]);
  }, [values, onFilterChange]);

  const handleInputChange = (index: number, event: React.ChangeEvent<HTMLInputElement>) => {
    const rawValue = event.target.value.replace(/\D/g, "");

    let numericValue = rawValue ? parseInt(rawValue, 10) : 0;

    
    if (numericValue < MIN) numericValue = MIN;
    if (numericValue > MAX) numericValue = MAX;

    setInputValues((prev) => {
      const newInputValues = [...prev];
      newInputValues[index] = numericValue.toString(); 
      return newInputValues;
    });
  };

  const handleInputBlur = (index: number) => {
    let newValue = Number(inputValues[index]);
    if (isNaN(newValue)) newValue = values[index];

    if (newValue < MIN) newValue = MIN;
    if (newValue > MAX) newValue = MAX;

    const newValues = [...values];
    newValues[index] = newValue;

    if (newValues[0] > newValues[1]) {
      newValues[index] = values[index];
    } else {
      setValues(newValues);
    }

    setInputValues(newValues.map((val) => val.toLocaleString()));
  };

  return (
    <VStack align= {isDesktop ? 'start' : 'center'} style={{ display: 'flex', flexDirection: 'column', rowGap: '20px' , marginLeft: '20px'}}>

      <Box display="flex" gapX={6}>
        <Input
          w={{ xl: '100%', lg: '100px' }}
          value={inputValues[0] + ' $'}
          onChange={(e) => handleInputChange(0, e)}
          onBlur={() => handleInputBlur(0)}
          type="text"
          borderRadius="180px"
          pl="14px" 
          minLength={MIN}
          maxLength={MAX}
        />
        <Input
          w={{ xl: '100%', lg: '100px' }}
          value={inputValues[1]+ ' $'}
          onChange={(e) => handleInputChange(1, e)}
          onBlur={() => handleInputBlur(1)}
          type="text"
          minLength={MIN}
          maxLength={MAX}
          borderRadius="180px"
          pl="14px"
        />
      </Box>

      <Box width={isDesktop ? '250px' : '50%'}>
        <Range
          values={values}
          step={STEP}
          min={MIN}
          max={MAX}
          onChange={(newValues) => {
            setValues(newValues);
            setInputValues(newValues.map((val) => val.toLocaleString()));
          }}
          renderTrack={({ props, children }) => (
            <Box
              {...props}
              height="6px"
              w={{ xl: '100%', lg: '70%' }}
              borderRadius="3px"
              background={getTrackBackground({
                values,
                colors: ["#ccc", `${colors.btnSecondColor}`, "#ccc"],
                min: MIN,
                max: MAX,
              })}
              alignSelf="center"
              marginTop="10px"
            >
              {children}
            </Box>
          )}
          renderThumb={({props}) => (
            <Box
              {...props}
              height="24px"
              width="24px"
              position={"relative"}
              top="0%"
              borderRadius="full"
              backgroundColor={colors.btnSecondColor}
              display="flex"
              justifyContent="center"
              alignItems="center"
              boxShadow="0px 2px 6px #AAA"
            />
          )}
        />
      </Box>
    </VStack>
  );
};