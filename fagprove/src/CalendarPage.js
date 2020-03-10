

import React, { useContext, useEffect } from 'react';
import { IsAdminContext, superRequest } from './App';
import { FlexDiv, FormLabel, TextSmall } from './BasicStyles';

import styled from 'styled-components';
import { Button } from './Button';
import { Link } from 'react-router-dom';
import { EditExaminationPage } from './EditExemenationPage';
import DatePicker from 'react-datepicker/es';




const Cell = styled.div`
margin-right: .5em;
width: 4em;
padding: .25em;
box-sizing: border-box;
`;


const ExaminationDiv = styled.div `
  background-color: yellow;
  :hover {
    cursor: pointer;
  }
`;
const NoDataDiv = styled.div`


:hover {
cursor: pointer;
}
`;






export function CalendarPage() {
  const [data, setData] = React.useState();
  const [fromDate, setFromDate] = React.useState(new Date().setDate(1));
  const [toDate, setToDate] = React.useState(new Date().setDate((new Date().getDate()+30)));
  const [editData, setEdit] = React.useState({
    isEditing: false,
    id: undefined,
  });
  const adminConsumer = useContext(IsAdminContext);
  const hasPemission = adminConsumer === 'ROLE_ADMIN' || adminConsumer === 'ROLE_MANAGER';
  // async function GetExaminations() {
  //   const fetch = await superRequest('http://localhost:8080/examination/list', {
  //   });
  //   const res = await fetch;
  //
  //   console.log(res);
  //
  // }
  async function getExaminators() {
    const fetch = await superRequest(' http://localhost:8080/examination/calendar', {
      "startDate": fromDate,
      "endDate": toDate
    });
    const res = await fetch;

    setData(res);

  }

  function setDateAsBusy(date, person) {
    console.log(date);
    console.log(date);
    console.log(person);
    const fromDate = new Date(date.year, date.month, date.day+1);
    const toDate = new Date(date.year, (date.month), (date.day+1));
     superRequest('http://localhost:8080/person/setBusy', {
      "person": person.id,
      "from": fromDate,
      "to": toDate,
    });
    window.location.reload();
  }


  useEffect(() => {
    // GetExaminations();
    getExaminators();
  }, [fromDate, toDate]);
  return (
    <div style={{width: '100%', margin: '0 auto'}}>
      {editData.isEditing ?
        <div style={{width: '100%' }}>
        <EditExaminationPage id={editData.id}/>
        </div> :

        <div>
          <FlexDiv style={{justifyContent: 'space-between',marginBottom: '5em'}}>
            <FlexDiv>
              <div>
                <FormLabel>Fra:</FormLabel>

              <DatePicker onSelect={(date)=> {
                setFromDate(date);
              }}  selected={fromDate}/>
              </div>
              <div style={{marginLeft: '1em'}}>
                <FormLabel>Til:</FormLabel>
              <DatePicker onSelect={(date)=> {
                setToDate(date);
              }} selected={toDate} />
              </div>
              </FlexDiv>
            {hasPemission &&
            <Link to={'/calendar/create/examination'}>
              <Button >Ny eksemenasjon</Button>
            </Link>
            }

          </FlexDiv>
          <div style={{margin: '0 auto', width: '90%'}}>
            <FlexDiv style={{}}>
              <Cell>Uke</Cell>
              <Cell>Veke dag</Cell>
              <Cell>år</Cell>
              <Cell>dato</Cell>
              <FlexDiv style={{marginLeft: '1em'}}>
                {data && data[0].examinators.map(examinator => {
                  return (
                    <FlexDiv style={{}}>
                      <Cell style={{width: '8em'}}>{examinator.name}</Cell>
                    </FlexDiv>
                  );
                })}
              </FlexDiv>
            </FlexDiv>
          <div style={{maxHeight: '60vh', overflow: 'auto'}} >
            {data && data.map((c) => {
              return (
                <FlexDiv style={{borderBottom: '1px solid #333'}}>
                  <div>
                    <div key={c.day}>
                      <FlexDiv style={{
                        marginRight: '1em'
                      }}>
                        <Cell>
                          {c.week}
                        </Cell>
                        <Cell style={{color: (c.dayOfWeek === 6 || c.dayOfWeek === 7) ? 'red': '#333'}}>
                          {c.dayOfWeek}
                        </Cell>
                        <Cell>
                          {c.year}
                        </Cell>
                        <Cell>
                          {c.day}
                        </Cell>
                      </FlexDiv>
                    </div>
                  </div> {c.examinators.map(ex => {
                    console.log(ex);
                  return (
                    <Cell style={{width: '8em', borderLeft: '1px solid #333'}}>
                      {ex.isBusy ?
                        <div style={{backgroundColor: 'red', color: '#fff'}}>Opptatt</div>:
                        ex.examinations && ex.examinations.length > 0 ?
                          <ExaminationDiv onClick={()=> {
                            if(hasPemission) {
                              setEdit({
                                isEditing: true,
                                id: ex.examinations[0].id,
                              })
                            }}
                          }>
                              <TextSmall>{ex.examinations[0].candidate.name}</TextSmall>
                              <TextSmall>{ex.examinations[0].candidate.region}</TextSmall>
                            </ExaminationDiv>: <NoDataDiv style={{height: '2em', width: '100%'}}
                            onClick={()=> {
                              if(hasPemission) {
                                if (window.confirm('sett som opptatt?')) {
                                  setDateAsBusy(c, ex);
                                }
                              }
                            }}
                          />
                      }

                    </Cell>
                  );
                })}
                </FlexDiv>
              );
            })}
          </div>

          </div>
        </div>
      }































    </div>
  )
}