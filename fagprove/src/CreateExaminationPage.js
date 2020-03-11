import React, { useEffect } from 'react';
import { IsJsonString, superRequest } from './App';
import { Dropdown } from './Dropdown';
import { FormLabel } from './BasicStyles';

import DatePicker, { registerLocale } from "react-datepicker";
import nb from 'date-fns/locale/nb';

import "react-datepicker/dist/react-datepicker.css";
import { Button } from './Button';
import Redirect from 'react-router-dom/es/Redirect';
import { string } from 'prop-types';


export function CreateExaminationPage() {
  const [candidates, setCandidates] = React.useState();
  const [examinators, setExaminators] = React.useState();
  const [showError, setShowError] = React.useState('');
  const [formData, setFormData] = React.useState({
    startDate: new Date(),
    endDate: new Date(),
  });


  async function getCandidates() {
    const fetch = await superRequest('http://localhost:8080/person/listCandidates', {});
    const res = await fetch;

    setCandidates(res);
    console.log(res);
  }

  async function getExaminators() {
    const fetch = await superRequest('http://localhost:8080/person/listExaminators', {});
    const res = await fetch;

    setExaminators(res);
    console.log(res);
  }


  useEffect(() => {
    getExaminators();
    getCandidates();
  }, []);

  registerLocale('nb', nb);
  return (

    <div style={{margin: '0 auto'}}>
      <form onSubmit={async (e) => {
        e.preventDefault();
        if (formData.candidate === undefined || formData.responsibleExaminator === undefined || formData.secondaryExaminator === undefined) {
          setShowError('Fyll in alle felta');
        } else {
          await superRequest(' http://localhost:8080/examination/create', {
            ...formData
          }).then(c => {

            if (typeof c === 'object') {
              return <Redirect to={'/calendar'}/>
            } else {
              setShowError(c);
            }
          }).catch(error => {
            console.log(error);
          });
        }
      }}>
        <div>
          <FormLabel>Kandidat</FormLabel>
          <Dropdown onChange={(candidate) => {
            setFormData({
              ...formData,
              candidate: candidate[0].value
            })
          }} multi={false} items={candidates && candidates?.map(c => {
            return (
              {value: c.id, label: c.name}
            );
          })}/>
        </div>
        <div style={{marginTop: '1em'}}>
          <FormLabel>Ansvarlig eksamninator</FormLabel>
          <Dropdown onChange={(value) => {
            setFormData({
              ...formData,
              responsibleExaminator: value[0].value
            })
          }} multi={false} items={examinators?.map(c => {
            return (
              {value: c.id, label: c.name}
            );
          })}/>
          <div style={{marginTop: '1em'}}>
            <FormLabel>sekunder eksamninator</FormLabel>
            <Dropdown onChange={(value) => {
              setFormData({
                ...formData,
                secondaryExaminator: value[0].value
              })
            }} multi={false} items={examinators?.map(c => {
              return (
                {value: c.id, label: c.name}
              );
            })}/>
          </div>
          <div style={{marginTop: '1em'}}>
            <FormLabel>start dato</FormLabel>
            <DatePicker
              selected={formData.startDate}
              onSelect={(date) => {
                if (date.getDay() === 0 || date.getDay() === 6) {
                  alert('datoen kan ikkje være på ein helgedag');
                } else {
                  setFormData({...formData, startDate: date})
                }
              }} //when day is clicked
              locale="nb"
            />
          </div>
          <div style={{marginTop: '1em'}}>
            <FormLabel>slutt dato</FormLabel>
            <DatePicker
              selected={formData.endDate}
              onSelect={(date) => {
                if (date.getDay() === 0 || date.getDay() === 6) {
                  alert('datoen kan ikkje være på ein helgedag');
                } else {
                  setFormData({...formData, endDate: date})

                }
              }}//when day is clicked
              locale="nb"
            />
          </div>
          <Button style={{marginTop: '1em'}} type={'submit'}>Opprett</Button>
        </div>

        {showError.length > 0 &&
        <div style={{color: 'red'}}>{typeof showError !== Object && showError}</div>
        }
      </form>

    </div>
  )
}











































